package com.kynsof.share.core.infrastructure.util;

import com.kynsof.share.core.domain.request.FileRequest;
import com.kynsof.share.core.domain.response.FileDto;
import com.kynsof.share.core.domain.response.ResponseStatus;
import com.kynsof.share.core.domain.response.UploadFileResponse;
import com.kynsof.share.core.domain.service.IAmazonClient;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("minio")
public class MinIOClient implements IAmazonClient {
    private static final Logger logger = LoggerFactory.getLogger(MinIOClient.class);
    private MinioClient minioClient;
    @Value("${minio.endpoint.url}")
    private String endpointUrl;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;

    @Getter
    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.bucket.private}")
    private Boolean isPrivateBucket;

    public MinIOClient() {
    }

    public MinioClient getMinioClient() {
        return this.minioClient;
    }

    @PostConstruct
    private void initializeMinIO() {
        logger.info(" Initializing MinIO connection...");
        this.minioClient = MinioClient.builder().endpoint(this.endpointUrl).credentials(this.accessKey, this.secretKey).build();
        logger.info("✅ MinIO connection initialized successfully.");
    }

    private String uploadFileWithMetadata(String originalFileName, byte[] bytes, String contentType, String bucketName) throws IOException {
        String objectKey = generateObjectKey(contentType);
        try (InputStream streamToUpload = new ByteArrayInputStream(bytes)) {
            long size = bytes.length;
            logger.info("Uploading '{}' (size: {} bytes, type: '{}') to MinIO...", objectKey, size, contentType);
            this.ensureBucketExists(bucketName);
            PutObjectArgs putObject = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(streamToUpload, size, -1)
                    .contentType(contentType)
                    .userMetadata(Map.of("originalFileName", originalFileName))
                    .build();
            this.minioClient.putObject(putObject);
            logger.info("✅ Successfully uploaded '{}'", objectKey);
        } catch (Exception e) {
            logger.error("❌ Error uploading file with metadata '{}': {}", originalFileName, e.getMessage());
            throw new IOException("Failed to upload file: " + originalFileName, e);
        }
        return objectKey;
    }

    public void ensureBucketExists(String bucketName) throws IOException {
        try {
            boolean bucketExists = this.minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!bucketExists) {
                logger.error("❌ Bucket '{}' does not exist in MinIO.", bucketName);
                throw new IOException("Bucket does not exist: " + bucketName);
            }
        } catch (MinioException e) {
            logger.error("❌ MinIO error while checking bucket existence: {}", bucketName, e);
            throw new IOException("Error checking bucket existence due to MinIO error", e);
        } catch (Exception e) {
            logger.error("❌ Unexpected error while checking bucket existence: {}", bucketName, e);
            throw new IOException("Error checking bucket existence", e);
        }
    }

    public String save(FileRequest fileRequest, String bucketName) throws IOException {
        String objectKey = this.uploadFileWithMetadata(fileRequest.getFileName(), fileRequest.getFile(), fileRequest.getContentType(), bucketName);
        return this.isPrivateBucket
                ? this.getPublicUrl(objectKey, bucketName)
                : this.endpointUrl + "/" + bucketName + "/" + objectKey;
    }

    public String save(FilePart filePart, String bucketName)  throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        filePart.content().toStream().forEach(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                throw new RuntimeException("Error writing file bytes", e);
            }
        });
        byte[] bytes = outputStream.toByteArray();
        String contentType = filePart.headers().getContentType() != null
                ? Objects.requireNonNull(filePart.headers().getContentType()).toString()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String originalFileName = filePart.filename();
        String objectKey = this.uploadFileWithMetadata(originalFileName, bytes, contentType, bucketName);
        return this.isPrivateBucket
                ? this.getPublicUrl(objectKey, bucketName)
                : this.endpointUrl + "/" + bucketName + "/" + objectKey;
    }

    public List<FileDto> saveAll(List<FileRequest> files, String bucketName) {
        logger.info("📦 Processing batch upload of {} files...", files.size());
        List<CompletableFuture<FileDto>> futures = files.stream().map(file -> CompletableFuture.supplyAsync(() -> {
            FileDto fileDto = new FileDto();
            fileDto.setId(file.getObjectId());
            fileDto.setName(file.getFileName());
            fileDto.setUploadFileResponse(new UploadFileResponse(ResponseStatus.SUCCESS_RESPONSE));

            try {
                String fileUrl = this.save(file, bucketName);
                fileDto.setUrl(fileUrl);
            } catch (IOException e) {
                fileDto.setUploadFileResponse(new UploadFileResponse(ResponseStatus.ERROR_RESPONSE, "UPLOAD_FAILED: " + e.getMessage()));
            }

            return fileDto;
        })).toList();
        List<FileDto> uploadedFiles = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        logger.info("📦 Batch upload completed. {} files processed.", uploadedFiles.size());
        return uploadedFiles;
    }

    public void delete(String url) {
        logger.info("📦 Deleting file: {}", url);
        if (url.isEmpty()) {
            logger.warn("⚠️ Skipping deletion: empty URL.");
        } else {
            if (url.contains(this.endpointUrl)) {
                url = url.replace(this.endpointUrl, "");
            }

            try {
                this.removeObject(url, this.bucketName);
                logger.info("✅ File '{}' deleted successfully.", url);
            } catch (Exception e) {
                logger.error("❌ Error deleting file: {}", url, e);
            }

        }
    }

    public byte[] downloadFile(String filePath, String bucketName) throws IOException {
        logger.info("📦 Downloading file from MinIO: {}", filePath);
        String objectKey = filePath;
        if (objectKey.startsWith("http")) {
            objectKey = objectKey.substring(objectKey.lastIndexOf("/") + 1);
        }
        try (InputStream inputStream = this.minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectKey)
                .build());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            inputStream.transferTo(outputStream);
            logger.info("✅ File downloaded successfully: {}", filePath);
            return outputStream.toByteArray();
        } catch (MinioException e) {
            logger.error("❌ MinIO error while downloading file: {}", filePath, e);
            throw new IOException("MinIO error while downloading file: " + filePath, e);
        } catch (Exception e) {
            logger.error("❌ Unexpected error while downloading file: {}", filePath, e);
            throw new IOException("Unexpected error while downloading file: " + filePath, e);
        }
    }

    private String getPublicUrl(String objectName, String bucketName) throws IOException {
        try {
            logger.info("📦 Generating public URL for file: {}", objectName);
            String url = this.minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(604800)
                    .build());
            if (url == null) {
                throw new IOException("Failed to generate public URL for: " + objectName);
            } else {
                logger.info("✅ Public URL generated: {}", url);
                return url;
            }
        } catch (Exception e) {
            logger.error("❌ Error generating public URL for: {}", objectName, e);
            throw new IOException(e);
        }
    }

    private void removeObject(String objectName, String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        RemoveObjectArgs req = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        this.minioClient.removeObject(req);
    }

    private String generateObjectKey(String contentType) {
        return UUID.randomUUID() + getExtensionFromContentType(contentType);
    }

    private String getExtensionFromContentType(String contentType) {
        return switch (contentType) {
            case MediaType.APPLICATION_XML_VALUE -> ".xml";
            case MediaType.APPLICATION_PDF_VALUE -> ".pdf";
            case "text/csv" -> ".csv";
            default -> ".dat";
        };
    }
}
