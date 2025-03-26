package com.kynsof.share.core.infrastructure.util;

import com.kynsof.share.core.domain.service.IAmazonClient;
import com.kynsof.share.utils.FileDto;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import io.minio.MinioClient;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service("minio")
public class MinIOClient implements IAmazonClient {

    private MinioClient minioClient;

    @Value("${minio.endpoint.url}")
    private String endpointUrl;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.bucket.private}")
    private Boolean isPrivateBucket;

    @PostConstruct
    private void initializeMinIO(){
        minioClient = MinioClient.builder()
                .endpoint(endpointUrl)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Override
    public void uploadFile(InputStream streamToUpload, Long size, String contentType, String objectKey)
            throws IOException {

        try {
            PutObjectArgs putObject = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(streamToUpload, size, -1)
                    .contentType(contentType)
                    .build();
            minioClient.putObject(putObject);
        }catch (ServerException | InsufficientDataException | ErrorResponseException |
                NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                XmlParserException | InternalException e){
            throw new IOException(e);
        }
    }

    @Override
    public String save(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();

        String sanitizedFilename = originalFilename.replace(" ", "_");

        String fileExtension = StringUtils.getFilenameExtension(sanitizedFilename);
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String name = StringUtils.stripFilenameExtension(sanitizedFilename) + "_" + timestamp + "." + fileExtension;

        this.uploadFile(file.getInputStream(), file.getSize(), file.getContentType(), name);

        if(isPrivateBucket){
            return getPublicUrl(name);
        }

        return endpointUrl + bucketName + "/" + name;
    }

    @Override
    public void delete(String url) {
        if (!url.isEmpty()) {
            String key = url;
            if (url.contains(this.endpointUrl)) {
                key = url.replace(this.endpointUrl, "");
            }

            try{
                removeObject(key);
            }catch (ServerException | InsufficientDataException | ErrorResponseException |
                    NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                    XmlParserException | InternalException | IOException ignored){}
        }
    }

    @Override
    public FileDto loadFile(String url) {
        String filename = url.replace(this.endpointUrl, "");

        return new FileDto(filename, url);
    }

    private String getPublicUrl(String objectName) throws IOException {
        try{
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(604800)
                    .build());
        }catch (ServerException | InsufficientDataException | ErrorResponseException |
                NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                XmlParserException | InternalException e) {
            throw new IOException(e);
        }
    }

    private void removeObject(String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        RemoveObjectArgs req = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        minioClient.removeObject(req);
    }
}