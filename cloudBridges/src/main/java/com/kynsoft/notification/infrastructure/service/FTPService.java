package com.kynsoft.notification.infrastructure.service;

import com.kynsof.share.core.domain.response.FileDto;
import com.kynsof.share.core.domain.response.ResponseStatus;
import com.kynsof.share.core.domain.response.UploadFileResponse;
import com.kynsoft.notification.domain.service.IFTPService;
import com.kynsoft.notification.infrastructure.config.FTPConfig;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class FTPService implements IFTPService {
    private static final Logger log = LoggerFactory.getLogger(FTPService.class);
    private final FTPConfig ftpConfig;

    public FTPService(FTPConfig ftpConfig) {
        this.ftpConfig = ftpConfig;
    }

    public void uploadFile(String remotePath, byte[] fileBytes, String fileName, String server, String user, String password, int port) {
        FTPClient ftpClient = new FTPClient();

        try {
            reconnectFTP(ftpClient, server, user, password);

            this.configureFTPClient(ftpClient);
            this.setRemotePath(ftpClient, remotePath);
            boolean uploaded = this.uploadFileWithRetry(ftpClient, fileName, fileBytes, null, server, user, password);
            if (!uploaded) {
                log.error("❌ FTP upload failed after multiple attempts for file: '{}'", fileName);

                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException ex) {
                    log.warn("⚠️ Error while disconnecting FTP client after failure: {}", ex.getMessage());
                }

                throw new RuntimeException("FTP upload failed for file: " + fileName);
            }
        } catch (SocketTimeoutException e) {
            log.warn("⚠️ FTP timeout: {}", e.getMessage());
            throw new RuntimeException("FTP timeout: " + fileName, e);
        } catch (IOException e) {
            log.error("❌ FTP connection error while uploading '{}': {}", fileName, e.getMessage(), e);
            throw new RuntimeException("FTP connection error while uploading file: " + fileName, e);
        } finally {
            this.disconnectFTP(ftpClient);
        }

    }

    public Mono<Void> uploadFilesBatch(String remotePath, List<FileDto> files, String server, String user, String password) {
        return Mono.fromCallable(() -> {
            int batchSize = this.ftpConfig.getMaxFilesPerBatch();
            List<List<FileDto>> batches = this.splitIntoBatches(files, batchSize);
            ExecutorService executor = Executors.newFixedThreadPool(this.ftpConfig.getMaxConcurrentUploads());
            try {
                CompletionService<Boolean> completionService = new ExecutorCompletionService<>(executor);

                for (List<FileDto> batch : batches) {
                    completionService.submit(() -> this.uploadBatch(remotePath, batch, server, user, password));
                }

                for (int i = 0; i < batches.size(); i++) {
                    try {
                        Future<Boolean> future = completionService.take();
                        Boolean result = future.get();
                        if (!result) {
                            log.warn("⚠️ A batch failed to upload.");
                        }
                    } catch (Exception e) {
                        log.error("❌ Error executing batch: {}", e.getMessage(), e);
                    }
                }
            } finally {
                executor.shutdown();
            }
            return null;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    private List<List<FileDto>> splitIntoBatches(List<FileDto> files, int batchSize) {
        List<List<FileDto>> batches = new ArrayList<>();

        for(int i = 0; i < files.size(); i += batchSize) {
            batches.add(files.subList(i, Math.min(i + batchSize, files.size())));
        }

        return batches;
    }

    private boolean uploadBatch(String remotePath, List<FileDto> batch, String server, String user, String password) {
        FTPClient ftpClient = new FTPClient();
        int attempts = 0;
        int maxRetries = 4;

        // Track files that failed previously
        List<FileDto> remainingToUpload = new ArrayList<>(batch);

        while (attempts < maxRetries && !remainingToUpload.isEmpty()) {
            try {
                reconnectFTP(ftpClient, server, user, password);

                configureFTPClient(ftpClient);
                setRemotePath(ftpClient, remotePath);

            } catch (SocketTimeoutException e) {
                log.warn("⚠️ Attempt {} failed due to FTP timeout: {}", attempts + 1, e.getMessage());
                attempts++;
                continue;
            } catch (IOException e) {
                log.error("❌ FTP error uploading batch: {}", e.getMessage(), e);
                attempts++;
                continue;
            }

            Iterator<FileDto> iterator = remainingToUpload.iterator();
            while (iterator.hasNext()) {
                FileDto file = iterator.next();

                // Skip already successful files
                if (file.getUploadFileResponse() != null && file.getUploadFileResponse().getStatus() == ResponseStatus.SUCCESS_RESPONSE) {
                    log.info("✅ Skipping already uploaded file: {}", file.getName());
                    iterator.remove(); // Remove from retry list
                    continue;
                }

                boolean uploaded = uploadFileWithRetry(ftpClient, file.getName(), file.getFileContent(), file, server, user, password);
                if (uploaded) {
                    iterator.remove(); // Remove successful uploads from retry list
                } else {
                    log.error("❌ Failed to upload file '{}' within batch.", file.getName());
                }
            }

            if (remainingToUpload.isEmpty()) {
                return true;
            }

            attempts++;
        }

        // If any files remain after all retries, mark them as failed
        if (!remainingToUpload.isEmpty()) {
            log.error("❌ Some files failed after {} attempts", maxRetries);
            markFilesAsFailed(remainingToUpload, "Failed after " + maxRetries + " attempts");
        }

        return remainingToUpload.isEmpty(); // Return true if all succeeded, false if any failed
    }

    private void configureFTPClient(FTPClient ftpClient) throws IOException {
        ftpClient.setFileType(2);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setBufferSize(this.ftpConfig.getBufferSize());
        ftpClient.setConnectTimeout(this.ftpConfig.getConnectTimeout());
        ftpClient.setSoTimeout(this.ftpConfig.getSoTimeout());
    }

    private void setRemotePath(FTPClient ftpClient, String remotePath) throws IOException {
        if (remotePath != null && !remotePath.trim().isEmpty() && !"/".equals(remotePath) && !"//".equals(remotePath)) {
            String currentDir = ftpClient.printWorkingDirectory();
            if (!remotePath.equals(currentDir) && !ftpClient.changeWorkingDirectory(remotePath) && (!ftpClient.makeDirectory(remotePath) || !ftpClient.changeWorkingDirectory(remotePath))) {
                throw new RuntimeException("❌ Failed to create/access the directory.");
            }
        }
    }

    private boolean uploadFileWithRetry(FTPClient ftpClient, String fileName, byte[] fileBytes, FileDto fileDto, String server,
                                        String user, String password) {
        int attempts = 0;
        int baseDelay = 1000;
        int maxDelay = 15000;
        int maxRetries = 4;
        while (attempts < maxRetries) {
            try {
                if (!ftpClient.isConnected() || !ftpClient.sendNoOp()) {
                    log.warn("🔄 Reconnecting to FTP server...");
                    reconnectFTP(ftpClient, server, user, password);
                }

                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes)) {
                    if (ftpClient.storeFile(fileName, inputStream)) {
                        log.info("✅ File '{}' successfully uploaded on attempt {}", fileName, attempts + 1);
                        if (fileDto != null) {
                            fileDto.setUploadFileResponse(new UploadFileResponse(ResponseStatus.SUCCESS_RESPONSE, "File uploaded successfully"));
                        }
                        return true;
                    }
                }
            } catch (IOException e) {
                log.warn("⚠️ Attempt {} failed for '{}': {}", attempts + 1, fileName, e.getMessage());
                if (fileDto != null) {
                    markFilesAsFailed(Collections.singletonList(fileDto), "Error uploading file: " + e.getMessage());
                }
            }

            attempts++;
            if (attempts < maxRetries) {
                int delay = Math.min(baseDelay * (1 << (attempts - 1)), maxDelay);
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

        log.error("❌ File '{}' upload failed after {} attempts", fileName, maxRetries);
        if (fileDto != null) {
            markFilesAsFailed(Collections.singletonList(fileDto), "Failed after " + maxRetries + " attempts");
        }

        return false;
    }

    private void reconnectFTP(FTPClient ftpClient, String server, String user, String password) throws IOException {
        if (ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
        ftpClient.connect(server, 21);
        if (!ftpClient.login(user, password)) {
            log.error("❌ Authentication failed for server: {}", server);
            throw new IOException("❌ Authentication failed while reconnecting to FTP.");
        }
    }

    private void disconnectFTP(FTPClient ftpClient) {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
                log.info("\ud83d\udd0c Successfully disconnected from FTP server.");
            }
        } catch (IOException ex) {
            log.warn("⚠️ FTP disconnection issue: {}", ex.getMessage(), ex);
        }

    }

    private void markFilesAsFailed(List<FileDto> files, String errorMessage) {
        files.forEach(fileDto ->
                fileDto.setUploadFileResponse(new UploadFileResponse(ResponseStatus.ERROR_RESPONSE, errorMessage))
        );
    }
}
