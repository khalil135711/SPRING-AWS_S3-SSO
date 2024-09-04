package largescaleit_demo.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import largescaleit_demo.models.Pet;
import largescaleit_demo.repositories.IPetRepository;

@RestController
public class PetController {
	// WE ARE NOT USING THIS CLASS !!
	IPetRepository petRepository;
	
	public PetController(IPetRepository petRepository) {
		this.petRepository = petRepository;
	}
	// WE ARE NOT USING THIS CLASS !!
	@GetMapping("/pets")
	public List<Pet> list(){
		return petRepository.getAllPets();
	}
	
	@GetMapping("/pets/{id}")
	public Pet get(@PathVariable("id") UUID id) {
		return petRepository.getPet(id);
	}
	
	@PostMapping("/pets")
	public Pet post(@RequestBody Pet pet) {
		petRepository.addPet(pet);
		return pet;
	}
	
	@PutMapping("/pets/{id}")
	public Pet put(@PathVariable("id") UUID id , @RequestBody Pet pet) {
		pet.id = id;
		petRepository.updatePet(pet);
		return petRepository.getPet(id);
	}
	
	@DeleteMapping("/pets/{id}")
	public void delete(@PathVariable("id") UUID id) {
		petRepository.removePet(id);
	}
}
