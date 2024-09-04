package largescaleit_demo.models;
import largescaleit_demo.models.produkt;


public class CartItem {
    private produkt product;
    private int quantity;

    
    public produkt getProduct() {
        return product;
    }

    public void setProduct(produkt product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}