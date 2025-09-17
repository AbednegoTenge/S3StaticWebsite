# S3 Static Website Uploader

This Java application automates the process of creating an AWS S3 bucket, uploading a static website file, setting the bucket policy to public, and enabling static website hosting.

## Features

- Creates an S3 bucket (if it does not exist)
- Uploads a local `index.html` file to the bucket
- Sets the bucket policy to allow public read access
- Enables static website hosting on the bucket

## Prerequisites

- Java 17 or higher
- Maven
- AWS account with credentials configured (using AWS CLI or `~/.aws/credentials`)
- The `website/index.html` file in your project resources

## Important: Public Access Settings

AWS now blocks public access to S3 buckets by default.  
**Before running this application, you must disable "Block Public Access" for your bucket.**  
You can do this using the AWS Console or with the AWS CLI:

```sh
aws s3api put-public-access-block \
  --bucket your-bucket-name \
  --public-access-block-configuration "BlockPublicAcls=false,IgnorePublicAcls=false,BlockPublicPolicy=false,RestrictPublicBuckets=false"
```

Replace `your-bucket-name` with your actual bucket name.

## Setup

1. Clone the repository.
2. Place your static website files (at least `index.html`) in the `website` directory under `src/main/resources/`.
3. Update the `BUCKETNAME` constant in `com.abednego.S3StaticWebsite.java` with your desired S3 bucket name.

## Build and Run

```sh
mvn clean package
java -cp target/your-jar-file.jar com.abednego.S3StaticWebsite
```

## Notes

- The application uses the AWS SDK for Java v2.
- Make sure your AWS credentials have permissions to create buckets, upload objects, and set bucket policies.
- You must disable "Block Public Access" for the bucket as described above for the website to be publicly accessible.

## Live Demo

Access the static website here:  
https://abednegotenge.s3.us-east-1.amazonaws.com/index.html

## License
MIT License