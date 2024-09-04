package largescaleit_demo.repositories;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import largescaleit_demo.models.Pet;
import largescaleit_demo.models.produkt;

public interface iProduktRep {
	
	List<produkt> getAllProdukt();
 
	produkt getProdukt(UUID idD);

	void removeProdukt(UUID idD);

	produkt addProdukt(produkt prd, MultipartFile imageFile);
	

	void updateProdukt(produkt prd);

}
