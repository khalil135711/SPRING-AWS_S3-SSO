package largescaleit_demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

import largescaleit_demo.models.Cart;
import largescaleit_demo.models.produkt;
import largescaleit_demo.repositories.CartService;
import largescaleit_demo.repositories.iProduktRep;
@Controller
public class ProduktController {

    @Autowired
    private iProduktRep produktRepository;
    CartService cartService;
    
    @Autowired
    public ProduktController(iProduktRep produktRepository, CartService cartService) {
        this.produktRepository = produktRepository;
        this.cartService = cartService;
    }

    @GetMapping("/products")
    public String getProducts(Model model) {
        List<produkt> products = produktRepository.getAllProdukt();
        products.forEach(product -> System.out.println("Print Product Test: " + product.nameD + ", " + product.typeD));
        model.addAttribute("products", products);
        return "products";
    }
    
    @GetMapping("/addproducts")
    public String addNewPro() {
        return "addProduct";
    }
        
    @PostMapping("/addProduct")
    public String addProduct(@RequestParam("nameD") String nameD,
                             @RequestParam("typeD") String typeD,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             @RequestParam("price") double price) { 
        produkt newProdukt = new produkt();
        newProdukt.nameD = nameD;
        newProdukt.typeD = typeD;
        newProdukt.price = price;  
        produktRepository.addProdukt(newProdukt, imageFile); 
        return "redirect:/products";
    }
    @PostMapping("/produkt")
    public String post(@RequestParam("nameD") String nameD,
                       @RequestParam("typeD") String typeD,
                       @RequestParam("imageFile") MultipartFile imageFile,
                       @RequestParam("price") double price,  
                       Model model) {

        produkt pro = new produkt();
        pro.idD = UUID.randomUUID();
        pro.nameD = nameD;
        pro.typeD = typeD;
        pro.price = price;  

        if (!imageFile.isEmpty()) {
            try {
                String fileName = imageFile.getOriginalFilename();
                Path path = Paths.get("uploaded-images/" + fileName);
                Files.write(path, imageFile.getBytes());
                pro.imageUrl = path.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        produkt savedProdukt = produktRepository.addProdukt(pro, imageFile);

        model.addAttribute("product", savedProdukt);
        model.addAttribute("message", "Product successfully added!");

        return "productSuccess";
    }

    @PostMapping("/addToCart")
    public String addToCart(@RequestParam("productId") UUID productId,
                             @RequestParam("quantity") int quantity,
                             HttpSession session) {
        produkt product = produktRepository.getProdukt(productId);
        
        if (product != null) {
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
                session.setAttribute("cart", cart);
            }
            
            cartService.addToCart(product, quantity, cart);
        }
        
        return "redirect:/cart"; 
    }
    
    @GetMapping("/productList")
    public String getProductsCart(Model model) {
    List<produkt> products = produktRepository.getAllProdukt();
    model.addAttribute("products", products);
    return "productList";
    }

    @GetMapping("/editProduct")
    public String editProduct(@RequestParam("idD") UUID idD, Model model) {
        produkt product = produktRepository.getProdukt(idD);
        model.addAttribute("product", product);
        return "editProduct";
    }
    
    @PostMapping("/updateProduct")
    public String updateProduct(@RequestParam("idD") UUID idD,
                                @RequestParam("nameD") String nameD,
                                @RequestParam("typeD") String typeD,
                                @RequestParam("price") double price) {  // Add price
        produkt updatedProduct = new produkt();
        updatedProduct.idD = idD;
        updatedProduct.nameD = nameD;
        updatedProduct.typeD = typeD;
        updatedProduct.price = price;  
        produktRepository.updateProdukt(updatedProduct);
        return "redirect:/products";
    }

    @GetMapping("/deleteProduct")
    public String deleteProduct(@RequestParam("idD") UUID idD) {
        produktRepository.removeProdukt(idD);
        return "redirect:/products";
    }
   
}

