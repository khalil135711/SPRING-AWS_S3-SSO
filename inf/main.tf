variable "container_registry"{
    type = string
}

variable "container_registry_user"{
    type = string
}

variable "container_registry_password"{
    type = string
} 

variable "container_image"{
    type = string
}

resource "random_pet" "name"{
}

locals{
    name=random_pet.name.id
}

resource "azurerm_resource_group" "rg" {
    location = "westus3"
    name = "rg_${local.name}"
}

output "resource_group_name"{
    value = azurerm_resource_group.rg.name
}

resource "azurerm_virtual_network" "network"{
    name = "net_${local.name}"
    resource_group_name = azurerm_resource_group.rg.name
    address_space = ["10.0.0.0/16"]
    location = azurerm_resource_group.rg.location
}

resource "azurerm_subnet" "subnet"{
    name = "subnet_${local.name}"
    resource_group_name = azurerm_resource_group.rg.name
    virtual_network_name = azurerm_virtual_network.network.name
    address_prefixes = ["10.0.1.0/24"]
}

resource "azurerm_network_security_group" "nsg"{
    name = "nsg_${local.name}"
    location = azurerm_resource_group.rg.location
    resource_group_name = azurerm_resource_group.rg.name

    security_rule{
        name = "SSH"
        priority = 1001
        direction = "Inbound"
        access = "Allow"
        protocol = "Tcp"
        source_port_range = "*"
        destination_port_range = "22"
        source_address_prefix = "*"
        destination_address_prefix = "*"
    }

    security_rule{
        name = "HTTP"
        priority = 1002
        direction = "Inbound"
        access = "Allow"
        protocol = "Tcp"
        source_port_range = "*"
        destination_port_range = "80"
        source_address_prefix = "*"
        destination_address_prefix = "*"
    }
}

resource "azurerm_public_ip" "ip" {
    name = "ip_${local.name}"
    location = azurerm_resource_group.rg.location
    resource_group_name = azurerm_resource_group.rg.name
    allocation_method = "Dynamic"
}

resource "azurerm_network_interface" "nic" {
    name = "nic_${local.name}"
    location = azurerm_resource_group.rg.location
    resource_group_name = azurerm_resource_group.rg.name

    ip_configuration {
        name = "nic_conf_${local.name}"
        subnet_id = azurerm_subnet.subnet.id
        private_ip_address_allocation = "Dynamic"
        public_ip_address_id = azurerm_public_ip.ip.id
    }
}

resource "azurerm_network_interface_security_group_association" "nic_sec" {
    network_interface_id = azurerm_network_interface.nic.id
    network_security_group_id = azurerm_network_security_group.nsg.id
}

data "template_cloudinit_config" "config" {
    part {
        content_type = "text/cloud-config"
        content = yamlencode({
            package_upgrade = false
            runcmd = [
                "curl -fsSL https:/get.docker.com -o get-docker.sh",
                "sudo sh get-docker.sh",
                "rm get-docker.sh",
                "echo ${var.container_registry_password} | docker login ${var.container_registry} -u ${var.container_registry_user} --password-stdin",
                "docker run -p80:8080 --restart always -d ${var.container_image}"
            ]
        })
    }
}

resource azurerm_linux_virtual_machine "vm" {
    name = "vm_${local.name}"
    location = azurerm_resource_group.rg.location
    resource_group_name = azurerm_resource_group.rg.name

    network_interface_ids = [azurerm_network_interface.nic.id]

    size = "Standard_B2ats_v2"

    os_disk{
        name = "disk_${local.name}"
        caching = "ReadWrite"
        storage_account_type = "Premium_LRS"
    }

    source_image_reference {
        publisher = "canonical"
        offer     = "ubuntu-24_04-lts"
        sku       = "server"
        version   = "latest"
    }

    computer_name = "${local.name}"
    admin_username = "azureuser"
    admin_password = "ThePassword!1234567"
    disable_password_authentication = false

    custom_data = data.template_cloudinit_config.config.rendered
}

output "public_ip_address" {
    value = azurerm_linux_virtual_machine.vm.public_ip_address
}