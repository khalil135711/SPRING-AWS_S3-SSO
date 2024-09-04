package largescaleit_demo.repositories;

import largescaleit_demo.models.Cart;
import largescaleit_demo.models.CartItem;
import largescaleit_demo.models.produkt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CartService {
    public void addToCart(produkt product, int quantity, Cart cart) {
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getIdD().equals(product.getIdD()))
                .findFirst()
                .orElse(null);
        if (existingItem != null) {
            
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }
    }
    
    public void updateCartItem(UUID productId, int quantity, Cart cart) {
        cart.getItems().stream()
            .filter(item -> item.getProduct().getIdD().equals(productId))
            .findFirst()
            .ifPresent(item -> item.setQuantity(quantity));
    }

    public void removeCartItem(UUID productId, Cart cart) {
        cart.getItems().removeIf(item -> item.getProduct().getIdD().equals(productId));
    }
}



