package largescaleit_demo.repositories;

import largescaleit_demo.models.CartItem;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CartRepository {

    
    private final Map<UUID, List<CartItem>> cartStorage = new HashMap<>();
    public void addItemToCart(UUID userId, CartItem item) {
        cartStorage.computeIfAbsent(userId, k -> new ArrayList<>()).add(item);
    }

    public void removeItemFromCart(UUID userId, UUID productId) {
        List<CartItem> items = cartStorage.get(userId);
        if (items != null) {
            items.removeIf(item -> item.getProduct().idD.equals(productId));
            if (items.isEmpty()) {
                cartStorage.remove(userId);
            }
        }
    }

    public List<CartItem> getItemsForUser(UUID userId) {
        return cartStorage.getOrDefault(userId, new ArrayList<>());
    }

    public void clearCartForUser(UUID userId) {
        cartStorage.remove(userId);
    }
}
