import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.nio.file.Paths;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.net.URL;


public class S3StaticWebsite {
    // Change these constants to your values
    private static final String BUCKETNAME = "abednegotenge";  // S3 bucket name must be globally unique
    private static final String FILEPATH = "website/index.html";  // Path to your local HTML file
    private static final String KEYNAME = "index.html";  // S3 object key name

    public static void main(String[] args) throws URISyntaxException {
        Region region = Region.US_EAST_1;  // Set your desired AWS region
        // Create S3 client with credentials from AWS profile
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        // create the bucket, upload the file, set public access and enable static web hosting
        createBucket(s3);
        uploadToBucket(s3);
        makeBucketPublic(s3);
        enableStaticWebHosting(s3);
    }

    // Create an S3 bucket method
    private static void createBucket(S3Client s3) {
        try {
            // Create the S3 bucket
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder().bucket(BUCKETNAME).build();
            s3.createBucket(createBucketRequest);
            System.out.println("Bucket created successfully");
        } catch (BucketAlreadyExistsException e) {
            // Handle the case where the bucket name is already taken
            System.err.println("Bucket name already taken, please choose a different name.");
        } catch (Exception e) {
            System.err.println("Error creating bucket: " + e.getMessage());
        }

    }

    // Upload a file to the S3 bucket method
    private static void uploadToBucket(S3Client s3) throws URISyntaxException {
        // load the file from the resources folder
        URL resource = S3StaticWebsite.class.getClassLoader().getResource(FILEPATH);
        if (resource == null) {
            throw new IllegalStateException("Resource not found: " + FILEPATH);
        }

        // Convert URL to Path
        Path filePath = Paths.get(resource.toURI());
        // Create a PutObjectRequest that specifies the bucket name and object key
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(S3StaticWebsite.BUCKETNAME)
                .key(S3StaticWebsite.KEYNAME)
                .build();

        // Upload the file to S3
        s3.putObject(putObjectRequest, RequestBody.fromFile(filePath));
        System.out.println("Object uploaded successfully: " + S3StaticWebsite.KEYNAME);
    }


    // Applies a bucket to make all objects in the bucket public
    private static void makeBucketPublic(S3Client s3) {
        // Define a bucket policy that allows public read access to all objects in the bucket
        String bucketPolicy = """
                    {
                      "Version": "2012-10-17",
                      "Statement": [
                        {
                          "Sid": "PublicReadGetObject",
                          "Effect": "Allow",
                          "Principal": "*",
                          "Action": "s3:GetObject",
                          "Resource": "arn:aws:s3:::%s/*"
                        }
                      ]
                    }
                    """.formatted(S3StaticWebsite.BUCKETNAME);

        // Apply the bucket policy
        PutBucketPolicyRequest putBucketPolicyRequest = PutBucketPolicyRequest.builder()
                .bucket(S3StaticWebsite.BUCKETNAME)
                .policy(bucketPolicy)
                .build();

        // Set the bucket policy to make the bucket public
        s3.putBucketPolicy(putBucketPolicyRequest);
        System.out.println("Bucket policy applied to make bucket public");
    }

    // Enables static web hosting on the S3 bucket
    private static void enableStaticWebHosting(S3Client s3) {
        // Configure the website with index and error documents
        WebsiteConfiguration websiteConfiguration = WebsiteConfiguration.builder()
                .indexDocument(IndexDocument.builder().suffix("index.html").build())
                .errorDocument(ErrorDocument.builder().key("error.html").build())
                .build();

        // Apply the website configuration to the bucket
        PutBucketWebsiteRequest websiteRequest =  PutBucketWebsiteRequest.builder()
                .bucket(S3StaticWebsite.BUCKETNAME)
                .websiteConfiguration(websiteConfiguration)
                .build();

        // Enable static website hosting
        s3.putBucketWebsite(websiteRequest);
        System.out.println("Static website enabled");

    }

}
