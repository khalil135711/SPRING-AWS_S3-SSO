terraform {
   required_version = ">=0.12"

   backend "http" {
   }

   required_providers {
        azurerm = {
            source  = "hashicorp/azurerm"
            version = "~>2.0"
        }
        random = {
            source  = "hashicorp/random"
            version = "~>3.0"
        }
        template = {
            source = "hashicorp/template"
            version = "2.2.0"
        }
   }
}

provider "azurerm"{
    features {}
}