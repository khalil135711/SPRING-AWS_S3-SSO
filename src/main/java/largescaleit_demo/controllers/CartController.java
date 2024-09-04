package largescaleit_demo.controllers;

import largescaleit_demo.models.Cart;
import largescaleit_demo.models.CartItem;
import largescaleit_demo.models.produkt;
import largescaleit_demo.repositories.CartService;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
public class CartController {
    @Autowired
    private CartService cartService;
    
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        }
        double totalPrice = cart.getItems().stream()
            .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
            .sum();
        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", totalPrice);
        return "cart"; 
    }

    @PostMapping("/cart/update")
    public String updateCartItem(@RequestParam("productId") UUID productId, @RequestParam("quantity") int quantity, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            cartService.updateCartItem(productId, quantity, cart);
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @PostMapping("/cart/delete")
    public String deleteCartItem(@RequestParam("productId") UUID productId, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            cartService.removeCartItem(productId, cart);
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session) {
       
        return "redirect:/checkout"; 
    }
}



