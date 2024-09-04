package largescaleit_demo.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.util.UUID;

public class testProdukt {

//  /*
//    private static final String BUCKET = "khalilkadarcloud";
//    private static final String PREFIX = "produkt/";
//    private static final String ACCESS_KEY = "AKIAQLSIVMHUWOK52O6M";
//    private static final String SECRET_KEY = "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";
//    private static final String ENDPOINT_URI = "https://khalilkadarcloud.s3.eu-north-1.amazonaws.com";
//    private static final Region REGION = Region.EU_NORTH_1;  // Adjust the region as necessary
//
//    public static void main(String[] args) {
//        
//        Produkt newProdukt = new Produkt();
//        newProdukt.idD = UUID.randomUUID();
//        newProdukt.nameD = "Test produkt13";
//        newProdukt.typeD = "Test Type13";
//
//        // Add the product using java 
//        testProdukt test = new testProdukt();
//        Produkt addedProdukt = test.addProdukt(newProdukt);
//
//       
//        if (addedProdukt != null) {
//            System.out.println("Product added with ID: " + addedProdukt.idD);
//        } else {
//            System.out.println("Failed to add product.");
//        }
//    }
//
//    public Produkt addProdukt(Produkt prd) {
//        try {
//            prd.idD = UUID.randomUUID();
//            ObjectMapper om = new ObjectMapper();
//            String prdJson = om.writeValueAsString(prd);
//
//            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
//
//            S3Client s3client = S3Client.builder()
//                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
//                    .region(REGION)
//                    .build();
//
//            s3client.putObject(PutObjectRequest.builder()
//                            .bucket(BUCKET)
//                            .key(PREFIX + prd.idD.toString())
//                            .build(),
//                    RequestBody.fromString(prdJson));
//
//            return prd;
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }*/
}


