package com.dropbox.DropboxTest.services.uploadservice.clients;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.RequestBody;

@Component
public class S3StoreClient implements IStoreClient {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3StoreClient(@Value("${aws.s3.access-key}") String accessKey,
                         @Value("${aws.s3.secret-key}") String secretKey,
                         @Value("${aws.s3.region}") String region) {
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(file.getContentType())
                .build();

        this.s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return uniqueFileName;
    }

    @Override
    public Boolean deleteFile(String key) throws IOException {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(key).build();
        DeleteObjectResponse response = this.s3Client.deleteObject(deleteObjectRequest);
        return response.deleteMarker();
    }

    public String createBucket(String name) {
        CreateBucketRequest createBucketRequest = CreateBucketRequest.builder().bucket(name).build();
        CreateBucketResponse response = this.s3Client.createBucket(createBucketRequest);
        return "";
    }

    public String getFileLink(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();
        ResponseInputStream<GetObjectResponse> responseStream = this.s3Client.getObject(getObjectRequest);
        GetObjectResponse response = responseStream.response();
        return "";
    }
}
