package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.ReportS3UploadResult;

import java.time.Duration;

public interface IReportS3Service {

    /**
     * Upload reporte a S3 con retry automático
     */
    ReportS3UploadResult uploadReportToS3(String serverRequestId, byte[] reportData,
                                          String fileName, String contentType);

    /**
     * Generar presigned URL para download
     */
    String generatePreSignedDownloadUrl(String s3ObjectKey, Duration expiration);

    /**
     * Verificar si un objeto existe en S3
     */
    boolean objectExists(String s3ObjectKey);

    /**
     * Eliminar objeto de S3
     */
    boolean deleteObject(String s3ObjectKey);

    /**
     * Generar S3 object key con estructura de carpetas
     */
    String generateS3ObjectKey(String serverRequestId, String fileName);

    /**
     * Verificar si S3 está disponible
     */
    boolean isS3Available();
}
