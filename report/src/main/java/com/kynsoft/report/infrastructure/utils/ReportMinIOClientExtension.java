package com.kynsoft.report.infrastructure.utils;

import com.kynsof.share.core.infrastructure.util.MinIOClient;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service("reportMinioClient")
public class ReportMinIOClientExtension {

    private static final Logger logger = LoggerFactory.getLogger(ReportMinIOClientExtension.class);
    private final MinIOClient minioClient;
    private final ReportS3ConfigurationProperties config;

    public ReportMinIOClientExtension(@Qualifier("minio") MinIOClient minioClient,
                                      ReportS3ConfigurationProperties config) {
        this.minioClient = minioClient;
        this.config = config;
    }

    /**
     * Upload con objectKey personalizado (no UUID)
     */
    public String saveWithCustomKey(String objectKey, byte[] bytes, String contentType, String bucketName) throws IOException {
        try (InputStream streamToUpload = new ByteArrayInputStream(bytes)) {
            long size = bytes.length;
            logger.info("Uploading report with custom key '{}' (size: {} bytes) to MinIO...", objectKey, size);

            ensureBucketExists(bucketName);

            PutObjectArgs putObject = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(streamToUpload, size, -1)
                    .contentType(contentType)
                    .build();

            minioClient.getMinioClient().putObject(putObject);
            logger.info("✅ Successfully uploaded report with key '{}'", objectKey);

            return objectKey;
        } catch (Exception e) {
            logger.error("❌ Error uploading report with key '{}': {}", objectKey, e.getMessage());
            throw new IOException("Failed to upload report: " + objectKey, e);
        }
    }

    public String generatePresignedDownloadUrl(String bucketName, String objectKey, int expiryHours) throws IOException {
        try {
            logger.info("📦 Generating presigned URL for report: {}", objectKey);

            String url = minioClient.getMinioClient().getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(expiryHours * 3600) // MinIO usa segundos
                            .build()
            );

            if (url == null) {
                throw new IOException("Failed to generate presigned URL for: " + objectKey);
            }

            logger.info("✅ Presigned URL generated for report: {}", objectKey);
            return url;

        } catch (Exception e) {
            logger.error("❌ Error generating presigned URL for: {}", objectKey, e);
            throw new IOException("Error generating presigned URL", e);
        }
    }

    public boolean objectExists(String bucketName, String objectKey) {
        try {
            minioClient.getMinioClient().getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            ).close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Agregar este método a tu ReportMinIOClientExtension
    public List<String> listObjectsWithPrefix(String bucketName, String prefix) {
        try {
            List<String> objectKeys = new ArrayList<>();

            Iterable<Result<Item>> results = minioClient.getMinioClient().listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(prefix)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                objectKeys.add(item.objectName());
            }

            logger.info("Found {} objects with prefix: {}", objectKeys.size(), prefix);
            return objectKeys;

        } catch (Exception e) {
            logger.error("Error listing objects with prefix: {} | Error: {}", prefix, e.getMessage());
            throw new RuntimeException("Error listing objects", e);
        }
    }

    public boolean deleteObject(String bucketName, String objectKey) {
        try {
            minioClient.getMinioClient().removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
            logger.info("✅ Report deleted successfully: {}", objectKey);
            return true;
        } catch (Exception e) {
            logger.error("❌ Error deleting report: {}", objectKey, e);
            return false;
        }
    }

    public boolean isReportsBucketAccessible() {
        try {
            return minioClient.getMinioClient().bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(config.getBucketName())
                            .build()
            );
        } catch (Exception e) {
            logger.error("❌ Error checking reports bucket accessibility", e);
            return false;
        }
    }

    private void ensureBucketExists(String bucketName) throws IOException {
        minioClient.ensureBucketExists(bucketName);
    }
}