package largescaleit_demo.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import largescaleit_demo.models.produkt;
import largescaleit_demo.repositories.iProduktRep;

@RestController
public class ProduktRestController {

    private final iProduktRep proRepository;

    @Autowired
    public ProduktRestController(iProduktRep proRepository) {
        this.proRepository = proRepository;
    }

    @GetMapping("/produkt/{idD}")
    public produkt get(@PathVariable("idD") UUID idD) {
        return proRepository.getProdukt(idD);
    }

    
    
    
    @PutMapping("/produkt/{idD}")
    public produkt put(@PathVariable("idD") UUID idD, @RequestBody produkt pet) {
        pet.idD = idD;
        proRepository.updateProdukt(pet);
        return proRepository.getProdukt(idD);
    }

    @DeleteMapping("/produkt/{idD}")
    public void delete(@PathVariable("idD") UUID idD) {
        proRepository.removeProdukt(idD);
    }
}

