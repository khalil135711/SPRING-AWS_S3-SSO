package largescaleit_demo.repositories;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import largescaleit_demo.models.Order;
import largescaleit_demo.models.Pet;
import largescaleit_demo.models.produkt;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.InvalidObjectStateException;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@Primary
public class S3ProRepository implements iProduktRep {

	final String BUCKET="fdf49c29-86ae-4402-acb3-b1c3cc0360ad";
	final String PREFIX="produkt/";
	
	final String ACCESS_KEY = "write_fdf49c29-86ae-4402-acb3-b1c3cc0360ad";
	final String SECRET_KEY = "fCI9FUrNacGR0adxRviS4gZjTgnzLFEH";
	final String ENDPOINT_URI = "https://coscine-s3-01.s3.fds.rwth-aachen.de:9021";
    S3Client s3client;
    AwsCredentials awsCredentials;
    public S3ProRepository() {
    	awsCredentials = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
        s3client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
                .region(Region.EU_NORTH_1)
                .endpointOverride(URI.create(ENDPOINT_URI))
                .forcePathStyle(true)
                .build();
    }
    
    @Override
    public List<produkt> getAllProdukt() {
        List<produkt> produkteList = new ArrayList<>();

        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(BUCKET)
                .prefix(PREFIX)
                .build();

        ListObjectsV2Response response = s3client.listObjectsV2(request);

        // Iterate through the objects
        for (S3Object s3Object : response.contents()) {
            produkt p = new produkt();
            String key = s3Object.key();
            
            // Extract the UUID from the key
            String uuidString = key.substring(PREFIX.length());
            try {
                p.idD = UUID.fromString(uuidString);
                
                byte[] objectBytes = s3client.getObject(GetObjectRequest.builder()
                        .bucket(BUCKET)
                        .key(key)
                        .build()).readAllBytes();
                
                ObjectMapper om = new ObjectMapper();
                p = om.readValue(objectBytes, produkt.class);

            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            produkteList.add(p);
        }

        return produkteList;
    }

    
    
    
    
    
/*     7ta lhna s7i7a hadi li comment
    @Override
    public List<produkt> getAllProdukt() {
        return listProdukte();
    }*/

    public List<produkt> listProdukte() {
        List<produkt> produkteList = new ArrayList<>();

        // Create a ListObjectsV2Request with the specified prefix
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(BUCKET)
                .prefix(PREFIX)
                .build();

        // Retrieve the list of objects from S3
        ListObjectsV2Response response = s3client.listObjectsV2(request);

        // Iterate through the objects
        for (S3Object s3Object : response.contents()) {
            produkt p = new produkt();
            String key = s3Object.key();
            
            // Assume the key contains the UUID in the format "produkt/uuid"
            String uuidString = key.substring(PREFIX.length());
            try {
                p.idD = UUID.fromString(uuidString);
                
            } catch (IllegalArgumentException e) {
                // Handle the case where UUID parsing fails
                continue;
            }

            // Placeholder for actual product details
            // For example, fetch the content or metadata to populate the product
            // Example:
            // p = fetchProduktDetails(s3Object.key());

            produkteList.add(p);
        }

        return produkteList;
    }

    // Placeholder method to fetch product details from S3 object if necessary
    // private produkt fetchProduktDetails(String key) {
    //     // Retrieve and parse object content or metadata
    //     return new produkt();
    // }

    @Override
    public produkt getProdukt(UUID idD) {
    	try {
			byte[] objectBytes = s3client.getObject(GetObjectRequest.builder()
	                .bucket("fdf49c29-86ae-4402-acb3-b1c3cc0360ad")
	                .key("produkt/" + idD.toString())
	                .build()
	        ).readAllBytes();
	        
	        ObjectMapper om = new ObjectMapper();
	        produkt p = om.readValue(objectBytes, produkt.class);
	        
	        return p;
		}catch(JsonProcessingException e) {
			return null;
		}catch(IOException e) {
			return null;
		}
    }
/* remove bla tswira 
    @Override
    public void removeProdukt(UUID idD) {
        // Implementation here
    	s3client.deleteObject(DeleteObjectRequest.builder()
				.bucket(BUCKET)
				.key(PREFIX + idD.toString())
				.build()
				);
    }
    */
    
    @Override
    public void removeProdukt(UUID idD) {
        // Delete product details
        s3client.deleteObject(DeleteObjectRequest.builder()
                .bucket(BUCKET)
                .key(PREFIX + idD.toString())
                .build()
        );

        // Delete product image
        s3client.deleteObject(DeleteObjectRequest.builder()
                .bucket(BUCKET)
                .key(PREFIX + idD.toString() + "/image")
                .build()
        );
    }
    
    
    
    @Override
    public produkt addProdukt(produkt prd, MultipartFile imageFile) {
        try {
            prd.idD = UUID.randomUUID();
            ObjectMapper om = new ObjectMapper();

           
            String imageKey = PREFIX + prd.idD.toString() + "/image";
            s3client.putObject(PutObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(imageKey)
                    .acl("public-read")  
                    .build(),
                    RequestBody.fromBytes(imageFile.getBytes())
            );

           
            prd.imageUrl = ENDPOINT_URI + "/" + BUCKET + "/" + imageKey;

            
            String productJson = om.writeValueAsString(prd);
            s3client.putObject(PutObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(PREFIX + prd.idD.toString())
                    .build(),
                    RequestBody.fromString(productJson)
            );

            return prd;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
   
    
    public void uploadOrderToS3(InputStream orderInputStream, String orderKey) throws IOException {
        // Generate a unique ID if not provided
        if (orderKey == null || orderKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Order key is null or empty");
        }

        // Check if the InputStream is valid
        if (orderInputStream == null || orderInputStream.available() == 0) {
            throw new IllegalArgumentException("InputStream is null or empty");
        }

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET)
                .key(orderKey)
                .build();

            s3client.putObject(
                putObjectRequest,
                RequestBody.fromInputStream(orderInputStream, orderInputStream.available())
            );

            System.out.println("Successfully uploaded order to S3 with key: " + orderKey);
        } catch (IOException e) {
            System.err.println("IOException during S3 upload: " + e.getMessage());
            e.printStackTrace();
            throw e; 
        }
    }
    public void uploadOrder(Order order) throws IOException {
        if (order.getId() == null) {
            order.setId(UUID.randomUUID()); 
        }

        String orderKey = "orders/" + order.getId().toString() + ".json";

        
        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = objectMapper.writeValueAsString(order);

        try (InputStream orderInputStream = new ByteArrayInputStream(orderJson.getBytes(StandardCharsets.UTF_8))) {
            uploadOrderToS3(orderInputStream, orderKey);
        }
    }


    //hadi khdama bla id
//    public void uploadOrderToS3(InputStream orderInputStream, String orderKey) throws IOException {
//        s3client.putObject(
//            PutObjectRequest.builder().bucket(BUCKET).key(orderKey).build(),
//            RequestBody.fromInputStream(orderInputStream, orderInputStream.available())
//        );
//    }

//    public void deleteOrderFromS3(UUID orderId) throws S3Exception {
//        String key = "orders/" + orderId.toString() + ".json";
//        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
//            .bucket(BUCKET)
//            .key(key)
//            .build();
//        s3client.deleteObject(deleteRequest);
//        System.out.println("Requested deletion for S3 object with key: " + key);
//    }


    public void deleteOrderFromS3(UUID orderId) {
        String key = "orders/" + orderId.toString() + ".json";
        System.out.println("Deleting order with key: " + key);
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder().bucket(BUCKET).key(key).build();
        s3client.deleteObject(deleteRequest);
    }
    
//    public void addOrder(Order order) throws IOException {
//        String key = "orders/" + UUID.randomUUID().toString() + ".json";
//        System.out.println("Adding order with key: " + key);
//        ObjectMapper objectMapper = new ObjectMapper();
//        byte[] orderBytes = objectMapper.writeValueAsBytes(order);
//        s3client.putObject(PutObjectRequest.builder().bucket(BUCKET).key(key).build(), RequestBody.fromBytes(orderBytes));
//    }

    public List<Order> getAllOrders() throws Exception {
        List<Order> orders = new ArrayList<>();
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(BUCKET)
                .prefix("orders/")
                .build();

        ListObjectsV2Response response = s3client.listObjectsV2(request);
        System.out.println("Found " + response.contents().size() + " orders.");
        for (S3Object s3Object : response.contents()) {
            String key = s3Object.key();
            System.out.println("Retrieving order with key: " + key);
            byte[] objectBytes = s3client.getObject(GetObjectRequest.builder().bucket(BUCKET).key(key).build()).readAllBytes();
            Order order = new ObjectMapper().readValue(objectBytes, Order.class);
            orders.add(order);
        }
        return orders;
    }



    @Override
    public void updateProdukt(produkt prd) {
    	try {
			produkt x = this.getProdukt(prd.idD);
			x.nameD = prd.nameD;
			x.typeD = prd.typeD;
			
			ObjectMapper om = new ObjectMapper();
			String petJson = om.writeValueAsString(prd);
			s3client.putObject(PutObjectRequest.builder()
					.bucket(BUCKET)
					.key(PREFIX + prd.idD.toString())
					.build(),
					RequestBody.fromString(petJson)
					);
		} catch (JsonProcessingException e) {
		}
    }
}
