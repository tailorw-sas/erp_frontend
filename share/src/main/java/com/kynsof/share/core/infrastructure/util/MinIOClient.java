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
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
    @Value("${minio.bucketName}")
    private String bucketName;
    @Value("${minio.bucket.private}")
    private Boolean isPrivateBucket;

    public MinIOClient() {
    }

    @PostConstruct
    private void initializeMinIO() {
        logger.info(" Initializing MinIO connection...");
        this.minioClient = MinioClient.builder().endpoint(this.endpointUrl).credentials(this.accessKey, this.secretKey).build();
        logger.info("✅ MinIO connection initialized successfully.");
    }

    public String getBucketName() {
        return this.bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public void uploadFile(InputStream streamToUpload, String contentType, String safeObjectKey) throws IOException {
        try {
            long size = streamToUpload.available();
            logger.info("Uploading '{}' (size: {} bytes, type: '{}') to MinIO...", safeObjectKey, size, contentType);
            this.ensureBucketExists();
            try {
                PutObjectArgs putObject = PutObjectArgs.builder()
                        .bucket(this.bucketName)
                        .object(safeObjectKey)
                        .stream(streamToUpload, size, -1L)
                        .contentType(contentType)
                        .build();
                this.minioClient.putObject(putObject);
                logger.info("✅ Successfully uploaded '{}'", safeObjectKey);
            } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | MinioException e) {
                logger.error("❌ Error uploading file '{}': {}", safeObjectKey, e.getMessage());
                throw new IOException("Failed to upload file: " + safeObjectKey, e);
            }
        } catch (Throwable var10) {
            if (streamToUpload != null) {
                try {
                    streamToUpload.close();
                } catch (Throwable var8) {
                    var10.addSuppressed(var8);
                }
            }

            throw var10;
        }

        if (streamToUpload != null) {
            streamToUpload.close();
        }
    }

    private void ensureBucketExists() throws IOException {
        try {
            boolean bucketExists = this.minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(this.bucketName)
                    .build());
            if (!bucketExists) {
                logger.error("❌ Bucket '{}' does not exist in MinIO.", this.bucketName);
                throw new IOException("Bucket does not exist: " + this.bucketName);
            }
        } catch (MinioException e) {
            logger.error("❌ MinIO error while checking bucket existence: {}", this.bucketName, e);
            throw new IOException("Error checking bucket existence due to MinIO error", e);
        } catch (Exception e) {
            logger.error("❌ Unexpected error while checking bucket existence: {}", this.bucketName, e);
            throw new IOException("Error checking bucket existence", e);
        }
    }

    public String save(FileRequest fileRequest) throws IOException {
        return this.save(fileRequest.getFile(), fileRequest.getFileName(), fileRequest.getContentType());
    }

    public String save(byte[] bytes, String fileName, String contentType) throws IOException {
        String objectKey = getName(fileName);

        try (InputStream fileStream = new ByteArrayInputStream(bytes)) {
            this.uploadFile(fileStream, contentType, objectKey);
            if (this.isPrivateBucket) {
                return this.getPublicUrl(objectKey);
            } else {
                return this.endpointUrl + "/" + this.bucketName + "/" + objectKey;
            }
        } catch (Exception e) {
            throw new IOException("Error saving file: " + e.getMessage(), e);
        }
    }

    public Mono<String> save(FilePart filePart) {
        return Mono.fromCallable(() -> {
            String objectName = getName(filePart.filename());

            try (InputStream inputStream = filePart.content().map(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);
                return bytes;
            }).collectList().map(byteArrays -> {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byteArrays.forEach(bytes -> {
                    try {
                        outputStream.write(bytes);
                    } catch (IOException e) {
                        throw new RuntimeException("Error writing bytes", e);
                    }
                });
                return new ByteArrayInputStream(outputStream.toByteArray());
            }).block()) {
                this.uploadFile(inputStream, filePart.headers().getContentType().toString(), objectName);
                if (this.isPrivateBucket) {
                    return this.getPublicUrl(objectName);
                } else {
                    return this.endpointUrl + "/" + this.bucketName + "/" + objectName;
                }
            } catch (Exception e) {
                throw new RuntimeException("Error saving file: " + e.getMessage(), e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public List<FileDto> saveAll(List<FileRequest> files) {
        logger.info("📦 Processing batch upload of {} files...", files.size());
        List<CompletableFuture<FileDto>> futures = files.stream().map(file -> CompletableFuture.supplyAsync(() -> {
            FileDto fileDto = new FileDto();
            fileDto.setId(file.getObjectId());
            fileDto.setName(file.getFileName());
            fileDto.setUploadFileResponse(new UploadFileResponse(ResponseStatus.SUCCESS_RESPONSE));

            try {
                String fileUrl = this.save(file);
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
                this.removeObject(url);
                logger.info("✅ File '{}' deleted successfully.", url);
            } catch (Exception e) {
                logger.error("❌ Error deleting file: {}", url, e);
            }

        }
    }

    public byte[] downloadFile(String filePath) {
        logger.info("📦 Downloading file from MinIO: {}", filePath);

        try {
            InputStream inputStream = this.minioClient.getObject(GetObjectArgs.builder()
                    .bucket(this.bucketName)
                    .object(filePath)
                    .build());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            inputStream.transferTo(outputStream);
            inputStream.close();
            logger.info("✅ File downloaded successfully: {}", filePath);
            return outputStream.toByteArray();
        } catch (MinioException e) {
            logger.error("❌ MinIO error while downloading file: {}", filePath, e);
        } catch (Exception e) {
            logger.error("❌ Unexpected error while downloading file: {}", filePath, e);
        }

        return null;
    }

    private String getPublicUrl(String objectName) throws IOException {
        try {
            logger.info("📦 Generating public URL for file: {}", objectName);
            String url = this.minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(this.bucketName)
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

    private void removeObject(String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        RemoveObjectArgs req = RemoveObjectArgs.builder()
                .bucket(this.bucketName)
                .object(objectName)
                .build();
        this.minioClient.removeObject(req);
    }

    public static String getName(String originalFilename) {
        String sanitizedFilename = originalFilename.replaceAll("[()\\s]", "_");
        String fileExtension = StringUtils.getFilenameExtension(sanitizedFilename);
        String timestamp = (new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")).format(new Date());
        return StringUtils.stripFilenameExtension(sanitizedFilename) + "_" + timestamp + "." + fileExtension;
    }
}
