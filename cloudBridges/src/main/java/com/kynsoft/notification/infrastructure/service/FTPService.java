package com.kynsoft.notification.infrastructure.service;

import com.kynsof.share.core.application.ftp.FtpService;
import com.kynsoft.notification.domain.service.IFTPService;
import com.kynsoft.notification.infrastructure.config.FTPConfig;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class FTPService implements IFTPService {

    private static final Logger log = LoggerFactory.getLogger(FtpService.class);
    private final FTPConfig ftpConfig;
    private final Random random = new Random();

    public FTPService(FTPConfig ftpConfig) {
        this.ftpConfig = ftpConfig;
    }

    public void uploadFile(String remotePath, byte[] fileBytes, String fileName, String server, String user, String password, int port) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            if (!ftpClient.login(user, password)) {
                log.error("❌ Authentication failed for server: {}", server);
                ftpClient.logout(); // Cerrar sesión antes de lanzar excepción
                throw new RuntimeException("Authentication failed with the FTP server.");
            }

            configureFTPClient(ftpClient);
            setRemotePath(ftpClient, remotePath);

            if (!uploadFileWithRetry(ftpClient, fileName, fileBytes, 4)) {
                log.error("❌ FTP upload failed for file: '{}'", fileName);
                throw new RuntimeException("FTP upload failed for file: " + fileName);
            }
        } catch (IOException e) {
            log.error("❌ FTP connection error while uploading '{}': {}", fileName, e.getMessage(), e);
            throw new RuntimeException("FTP connection error while uploading file: " + fileName, e);
        } finally {
            disconnectFTP(ftpClient);
        }
    }

    public Mono<List<Map<String, String>>> uploadFilesBatch(String remotePath, List<FilePart> files, String server, String user, String password,
                                                            int port) {
        return Mono.fromCallable(() -> {
            FTPClient ftpClient = new FTPClient();
            List<Map<String, String>> uploadResults = new ArrayList<>();

            try {
                // Connect and authenticate
                ftpClient.connect(server, port);
                if (!ftpClient.login(user, password)) {
                    log.error("❌ Authentication failed for server: {}", server);
                    throw new RuntimeException("Authentication failed with the FTP server.");
                }

                configureFTPClient(ftpClient);
                setRemotePath(ftpClient, remotePath);

                // Process files in batches
                List<List<FilePart>> batches = partitionList(files, 10);
                CountDownLatch latch = new CountDownLatch(files.size());

                for (List<FilePart> batch : batches) {
                    for (FilePart file : batch) {
                        // Use the improved extractBytesFromFilePart method
                        extractBytesFromFilePart(file)
                                .subscribe(fileBytes -> {
                                    try {
                                        boolean success = uploadFileWithRetry(ftpClient, file.filename(), fileBytes, 4);

                                        Map<String, String> fileResult = new HashMap<>();
                                        fileResult.put("fileName", file.filename());
                                        fileResult.put("status", success ? "success" : "failed");
                                        if (!success) {
                                            log.error("❌ Failed to upload file '{}'", file.filename());
                                            fileResult.put("error", "Upload failed after retries");
                                        }

                                        synchronized (uploadResults) {
                                            uploadResults.add(fileResult);
                                        }
                                    } catch (Exception e) {
                                        log.error("❌ Error uploading file '{}': {}", file.filename(), e.getMessage());

                                        Map<String, String> fileResult = new HashMap<>();
                                        fileResult.put("fileName", file.filename());
                                        fileResult.put("status", "failed");
                                        fileResult.put("error", e.getMessage());

                                        synchronized (uploadResults) {
                                            uploadResults.add(fileResult);
                                        }
                                    } finally {
                                        latch.countDown();
                                    }
                                }, error -> {
                                    log.error("❌ Error extracting bytes from file '{}': {}", file.filename(), error.getMessage());

                                    Map<String, String> fileResult = new HashMap<>();
                                    fileResult.put("fileName", file.filename());
                                    fileResult.put("status", "failed");
                                    fileResult.put("error", "Failed to read file: " + error.getMessage());

                                    synchronized (uploadResults) {
                                        uploadResults.add(fileResult);
                                    }

                                    latch.countDown();
                                });
                    }
                }

                // Wait for all file operations to complete
                try {
                    // Set a reasonable timeout to prevent indefinite waiting
                    if (!latch.await(5, TimeUnit.MINUTES)) {
                        log.warn("⚠️ Timeout waiting for file uploads to complete");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("❌ Upload process was interrupted", e);
                }

                return uploadResults;
            } catch (IOException e) {
                log.error("❌ FTP connection error: {}", e.getMessage(), e);
                throw new RuntimeException("FTP connection error while uploading files", e);
            } finally {
                disconnectFTP(ftpClient);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }


    private List<List<FilePart>> partitionList(List<FilePart> list, int batchSize) {
        List<List<FilePart>> partitionedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            partitionedList.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return partitionedList;
    }

    private void configureFTPClient(FTPClient ftpClient) throws IOException {
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setBufferSize(ftpConfig.getBufferSize());
        ftpClient.setConnectTimeout(ftpConfig.getConnectTimeout());
        ftpClient.setSoTimeout(ftpConfig.getSoTimeout());
    }

    private void setRemotePath(FTPClient ftpClient, String remotePath) throws IOException {
        if (remotePath == null || remotePath.trim().isEmpty() || "/".equals(remotePath) || "//".equals(remotePath)) {
            return;
        }

        String currentDir = ftpClient.printWorkingDirectory();
        if (!remotePath.equals(currentDir)) {
            if (!ftpClient.changeWorkingDirectory(remotePath)) {
                if (!ftpClient.makeDirectory(remotePath) || !ftpClient.changeWorkingDirectory(remotePath)) {
                    throw new RuntimeException("❌ Failed to create/access the directory.");
                }
            }
        }
    }

    private boolean uploadFileWithRetry(FTPClient ftpClient, String fileName, byte[] fileBytes, int maxRetries) {
        int attempts = 0;
        int baseDelay = 1000; // 1 segundo en ms
        int maxDelay = 30000; // 30 segundos en ms

        while (attempts < maxRetries) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes)) {
                if (ftpClient.storeFile(fileName, inputStream)) {
                    log.info("✅ File '{}' uploaded successfully on attempt {}", fileName, attempts + 1);
                    return true;
                }
            } catch (IOException e) {
                log.warn("⚠️ Attempt {} failed for '{}': {}", attempts + 1, fileName, e.getMessage());
            }

            attempts++;
            if (attempts < maxRetries) {
                int delay = Math.min(baseDelay * (1 << (attempts - 1)), maxDelay);
                delay += random.nextInt(500); // Jitter aleatorio para evitar colisiones
                log.info("⏳ Waiting {} ms before next retry...", delay);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("⚠️ Retry wait interrupted", ie);
                    return false;
                }
            }
        }

        log.error("❌ Failed to upload the file '{}' after {} attempts", fileName, maxRetries);
        return false;
    }

    private void disconnectFTP(FTPClient ftpClient) {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException ex) {
            log.error("⚠️ Error while closing FTP connection: {}", ex.getMessage(), ex);
        }
    }

    private Mono<byte[]> extractBytesFromFilePart(FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    try {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer); // Important: release the buffer to avoid memory leaks
                        return bytes;
                    } catch (Exception e) {
                        log.error("❌ Error reading file '{}' content: {}", filePart.filename(), e.getMessage());
                        throw new RuntimeException("Failed to read file content", e);
                    }
                })
                .onErrorResume(e -> {
                    log.error("❌ Failed to extract bytes from '{}'", filePart.filename(), e);
                    return Mono.just(new byte[0]);
                });
    }
}