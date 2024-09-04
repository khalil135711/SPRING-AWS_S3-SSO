package largescaleit_demo.repositories;

import java.util.List;
import java.util.UUID;

import largescaleit_demo.models.Pet;

public interface IPetRepository {

	
	List<Pet> getAllPets();

	Pet getPet(UUID id);

	void removePet(UUID id);

	Pet addPet(Pet pet);

	void updatePet(Pet pet);

	
}