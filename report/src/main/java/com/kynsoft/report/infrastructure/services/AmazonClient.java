package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.services.IAmazonClient;
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
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AmazonClient implements IAmazonClient {

    private S3Client s3Client;
    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.cloudfront.domain}")
    private String cloudfrontDomain;

    @PostConstruct
    private void initializeAmazon() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        s3Client = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.US_EAST_2).build();
    }

    @Override
    public void uploadFile(InputStream streamToUpload, Long size, String contentType, String objectKey)
            throws AwsServiceException, SdkClientException, IOException {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(this.bucketName).key(objectKey).contentType(contentType).contentDisposition("inline").build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(streamToUpload, streamToUpload.available()));

    }

    @Override
    public String save(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();

        String sanitizedFilename = originalFilename.replace(" ", "_");

        String fileExtension = StringUtils.getFilenameExtension(sanitizedFilename);
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String name = StringUtils.stripFilenameExtension(sanitizedFilename) + "_" + timestamp + "." + fileExtension;
        this.uploadFile(file.getInputStream(), file.getSize(), file.getContentType(), name);

        String objectUrl = this.cloudfrontDomain + name;

        return objectUrl;
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

}
