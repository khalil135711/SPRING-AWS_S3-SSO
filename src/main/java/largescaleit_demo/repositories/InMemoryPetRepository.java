package largescaleit_demo.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import largescaleit_demo.models.Pet;

@Service
public class InMemoryPetRepository implements IPetRepository {
	static Map<UUID, Pet> pets = new HashMap<>();
	
	@Override
	public List<Pet> getAllPets(){
		return new ArrayList<>(pets.values());
	}
	@Override
	public Pet getPet(UUID id) {
		return pets.get(id);
	}
	
	@Override
	public void removePet(UUID id) {
		pets.remove(id);
	}
	
	@Override
	public Pet addPet(Pet pet) {
		pet.id = UUID.randomUUID();
		pets.put(pet.id, pet);
		return pet;
	}
	@Override
	public void updatePet(Pet pet) {
		Pet x = pets.get(pet.id);
		x.name = pet.name;
		x.kind = pet.kind;
	}
}
