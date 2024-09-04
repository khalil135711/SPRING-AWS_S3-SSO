package largescaleit_demo.repositories;

import java.util.List;
import largescaleit_demo.models.produkt;

public class ListProdukteTest {
    public static void main(String[] args) {
        
        S3ProRepository repository = new S3ProRepository();
       
        List<produkt> produkteList = repository.listProdukte();

        if (produkteList != null && !produkteList.isEmpty()) {
            for (produkt p : produkteList) {
                System.out.println("Product ID: " + p.idD);
                System.out.println("Product Name: " + p.nameD);  
                System.out.println("Product Type: " + p.typeD);  
                System.out.println("---------------------------");
            }
        } else {
            System.out.println("No products found.");
        }
    }
}
