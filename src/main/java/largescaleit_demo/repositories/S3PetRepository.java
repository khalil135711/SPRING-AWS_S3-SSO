package largescaleit_demo.repositories;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import largescaleit_demo.models.Pet;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@Primary
public class S3PetRepository implements IPetRepository {
	final String BUCKET="fdf49c29-86ae-4402-acb3-b1c3cc0360ad";
	final String PREFIX="pets/";
	
	final String ACCESS_KEY = "write_fdf49c29-86ae-4402-acb3-b1c3cc0360ad";
	final String SECRET_KEY = "fCI9FUrNacGR0adxRviS4gZjTgnzLFEH";
	final String ENDPOINT_URI = "https://coscine-s3-01.s3.fds.rwth-aachen.de:9021";
	
	
	S3Client s3client;
	AwsCredentials awsCredentials;
		
	public S3PetRepository(){
		awsCredentials = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
		s3client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
                .region(Region.EU_NORTH_1)
                .endpointOverride(URI.create(ENDPOINT_URI))
                .forcePathStyle(true)
                .build();
	}
	
	@Override
	public List<Pet> getAllPets() {
		List<S3Object> objects = s3client.listObjects(ListObjectsRequest.builder()
				.bucket(BUCKET)
				.prefix(PREFIX)
				.build()).contents();
		
		List<Pet> pets = new ArrayList<>();
		
		for(S3Object o : objects) {
			Pet p = new Pet();
			p.id = UUID.fromString(o.key().substring(PREFIX.length()));
			pets.add(p);
		}
		
		return pets;
	}

	@Override
	public Pet getPet(UUID id) {
		try {
			byte[] objectBytes = s3client.getObject(GetObjectRequest.builder()
	                .bucket("fdf49c29-86ae-4402-acb3-b1c3cc0360ad")
	                .key("pets/" + id.toString())
	                .build()
	        ).readAllBytes();
	        
	        ObjectMapper om = new ObjectMapper();
	        Pet p = om.readValue(objectBytes, Pet.class);
	        
	        return p;
		}catch(JsonProcessingException e) {
			return null;
		}catch(IOException e) {
			return null;
		}
	}

	@Override
	public void removePet(UUID id) {
		s3client.deleteObject(DeleteObjectRequest.builder()
				.bucket(BUCKET)
				.key(PREFIX + id.toString())
				.build()
				);
	}

	@Override
	public Pet addPet(Pet pet) {
		try {
			pet.id = UUID.randomUUID();
			ObjectMapper om = new ObjectMapper();
			String petJson = om.writeValueAsString(pet);
			s3client.putObject(PutObjectRequest.builder()
					.bucket(BUCKET)
					.key(PREFIX + pet.id.toString())
					.build(),
					RequestBody.fromString(petJson)
					);
			return pet;
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	@Override
	public void updatePet(Pet pet) {
		try {
			Pet x = this.getPet(pet.id);
			x.name = pet.name;
			x.kind = pet.kind;
			
			ObjectMapper om = new ObjectMapper();
			String petJson = om.writeValueAsString(pet);
			s3client.putObject(PutObjectRequest.builder()
					.bucket(BUCKET)
					.key(PREFIX + pet.id.toString())
					.build(),
					RequestBody.fromString(petJson)
					);
		} catch (JsonProcessingException e) {
		}	
	}

}
