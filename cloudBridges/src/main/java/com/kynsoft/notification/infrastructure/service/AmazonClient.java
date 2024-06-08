package com.kynsoft.notification.infrastructure.service;

import com.kynsoft.notification.domain.dto.AFileDto;
import com.kynsoft.notification.domain.dto.FileInfoDto;
import com.kynsoft.notification.domain.service.IAmazonClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AmazonClient implements IAmazonClient {

    private S3Client s3Client;
    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.bucketUrl}")
    private String bucketUrl;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.cloudfront.domain}")
    private String cloudfrontDomain;

    @PostConstruct
    private void initializeAmazon() {
//        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
//        s3Client = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(credentials))
//                .region(Region.US_EAST_2).build();

        this.s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        accessKey, secretKey)))
                .endpointOverride(URI.create(bucketUrl))
                .region(Region.of(region))  // Esta región es simbólica en este contexto
                .build();
    }

    @Override
    public void uploadFile(InputStream streamToUpload, Long size, String contentType, String objectKey)
            throws AwsServiceException, SdkClientException, IOException {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(this.bucketName).key(objectKey).contentType(contentType).contentDisposition("inline").build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(streamToUpload, streamToUpload.available()));

    }


    public void uploadFileV1(InputStream streamToUpload, Long size, String contentType, String objectKey)
            throws AwsServiceException, SdkClientException, IOException {

//        PutObjectRequest putObjectRequest = PutObjectRequest
//                .builder().bucket(this.bucketName)
//                .key(objectKey)
//                .build();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(this.bucketName)
                .key(objectKey).contentType(contentType).contentDisposition("inline").build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(streamToUpload, streamToUpload.available()));

    }

    @Override
    public String save(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();

        String sanitizedFilename = originalFilename.replace(" ", "_");

        String fileExtension = StringUtils.getFilenameExtension(sanitizedFilename);
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String name = StringUtils.stripFilenameExtension(sanitizedFilename) + "_" + timestamp + "." + fileExtension;
        this.uploadFileV1(file.getInputStream(), file.getSize(), file.getContentType(), name);

        return this.cloudfrontDomain + name;
    }

    @Override
    public void delete(String url) {
        if (!url.equals("")) {

            String key = url;

            if (url != null && url.contains(this.cloudfrontDomain)) {
                key = url.replace(this.cloudfrontDomain, "");
            }

            DeleteObjectRequest req = DeleteObjectRequest.builder().bucket(this.bucketName).key(key).build();
            this.s3Client.deleteObject(req);
        }
    }

    @Override
    public AFileDto loadFile(String url) {
        String filename = url.replace(this.cloudfrontDomain, "");

        return new AFileDto(filename, url);
    }

    public List<FileInfoDto> listAllFiles(String bucketName) {
        ListObjectsV2Request req = ListObjectsV2Request.builder().bucket(bucketName).build();
        ListObjectsV2Response result = s3Client.listObjectsV2(req);

        return result.contents().stream()
                .map(s3Object -> new FileInfoDto(s3Object.key(), this.cloudfrontDomain))
                .collect(Collectors.toList());
    }

    public void deleteFileByKey(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
        s3Client.deleteObject(deleteObjectRequest);
        }catch (Exception e){
            e.printStackTrace();
            String mesage = e.getMessage();
        }

    }

    public List<String> listAllBuckets() {
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
        List<Bucket> buckets = listBucketsResponse.buckets();

        return buckets.stream()
                .map(Bucket::name)
                .collect(Collectors.toList());
    }
}
