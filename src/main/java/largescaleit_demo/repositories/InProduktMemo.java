package largescaleit_demo.repositories;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import largescaleit_demo.models.produkt;

@Service
@Qualifier("inProduktMemo")
public class InProduktMemo implements iProduktRep{
	
	static Map<UUID, produkt> pro = new HashMap<>();
	@Override
	public List<produkt> getAllProdukt() {
		
		return new ArrayList<>(pro.values());
	}

	@Override
	public produkt getProdukt(UUID idD) {
		
		return pro.get(idD);
	}

	@Override
	public void removeProdukt(UUID idD) {
		
		pro.remove(idD);
		
	}
	@Override
	public void updateProdukt(produkt prd) {
		produkt x = pro.get(prd.idD);
		x.nameD = prd.nameD;
		x.typeD = prd.typeD;
		
	}
@Override
public produkt addProdukt(produkt prd,MultipartFile imageFile) {
    prd.idD = UUID.randomUUID();
    pro.put(prd.idD, prd);
    return prd;
}

}
