package com.kynsoft.notification.infrastructure.service;

import com.kynsof.share.core.application.ftp.FtpService;
import com.kynsoft.notification.domain.service.IFTPService;
import com.kynsoft.notification.infrastructure.config.FTPConfig;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class FTPService implements IFTPService {

    private static final Logger log = LoggerFactory.getLogger(FtpService.class);
    private final FTPConfig ftpConfig;

    public FTPService(FTPConfig ftpConfig) {
        this.ftpConfig = ftpConfig;
    }

    public void uploadFile(String remotePath, InputStream inputStream, String fileName, String server, String user,
                           String password, int port) {
        FTPClient ftpClient = new FTPClient();
        try {
            log.info("🔗 Connecting to FTP server: {} on port {}", server, port);
            ftpClient.connect(server, port);

            if (!ftpClient.login(user, password)) {
                throw new RuntimeException("❌ Authentication failed with the FTP server.");
            }

            log.info("✅ Successfully authenticated with the FTP server.");

            // FTP settings
            configureFTPClient(ftpClient);

            // Validate and change directory
            remotePath = validateAndSetRemotePath(ftpClient, remotePath);

            // Convert InputStream properly
            try (ByteArrayInputStream newInputStream = convertInputStream(inputStream)) {
                if (ftpClient.storeFile(fileName, newInputStream)) {
                    log.info("✅ File '{}' successfully uploaded to '{}'.", fileName, remotePath);
                } else {
                    throw new RuntimeException("❌ Failed to upload the file.");
                }
            }

        } catch (IOException e) {
            log.error("❌ FTP connection error: {}", e.getMessage(), e);
            throw new RuntimeException("FTP connection error: " + e.getMessage(), e);
        } finally {
            disconnectFTP(ftpClient);
        }
    }

    public InputStream downloadFile(String remoteFilePath, String server, String user, String password, int port) {
        FTPClient ftpClient = new FTPClient();
        try {
            log.info("🔗 Connecting to FTP server: {} on port {}", server, port);
            ftpClient.connect(server, port);

            if (!ftpClient.login(user, password)) {
                throw new RuntimeException("❌ Authentication failed with the FTP server.");
            }

            log.info("✅ Successfully authenticated with the FTP server.");

            configureFTPClient(ftpClient);

            log.info("📂 Downloading file: {}", remoteFilePath);
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFilePath);

            if (inputStream == null) {
                throw new RuntimeException("❌ Failed to retrieve file from FTP server.");
            }

            log.info("✅ File '{}' successfully retrieved from FTP server.", remoteFilePath);
            return new BufferedInputStream(inputStream);

        } catch (IOException e) {
            log.error("❌ FTP connection error: {}", e.getMessage(), e);
            throw new RuntimeException("FTP connection error: " + e.getMessage(), e);
        } finally {
            disconnectFTP(ftpClient);
        }
    }

    private void configureFTPClient(FTPClient ftpClient) throws IOException {
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setBufferSize(ftpConfig.getBufferSize());
        ftpClient.setConnectTimeout(ftpConfig.getConnectTimeout());
        ftpClient.setSoTimeout(ftpConfig.getSoTimeout());
    }

    private String validateAndSetRemotePath(FTPClient ftpClient, String remotePath) throws IOException {
        if (remotePath == null || remotePath.trim().isEmpty() || "/".equals(remotePath) || "//".equals(remotePath)) {
            log.warn("⚠️ Invalid remotePath '{}', using default FTP directory.", remotePath);
            return ftpClient.printWorkingDirectory();
        }

        if (!remotePath.equals(ftpClient.printWorkingDirectory())) {
            log.info("📂 Changing to directory: {}", remotePath);
            if (!ftpClient.changeWorkingDirectory(remotePath)) {
                log.warn("⚠️ Directory '{}' does not exist. Creating it...", remotePath);
                if (!ftpClient.makeDirectory(remotePath) || !ftpClient.changeWorkingDirectory(remotePath)) {
                    throw new RuntimeException("❌ Failed to create/access the directory.");
                }
                log.info("✅ Successfully created and accessed '{}'.", remotePath);
            }
        } else {
            log.info("📂 Using default FTP directory.");
        }
        return remotePath;
    }

    private ByteArrayInputStream convertInputStream(InputStream inputStream) throws IOException {
        if (inputStream instanceof ByteArrayInputStream) {
            return (ByteArrayInputStream) inputStream;
        }

        log.info("🔄 Converting InputStream to ByteArrayInputStream...");
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            inputStream.transferTo(byteArrayOutputStream);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }
    }

    private void disconnectFTP(FTPClient ftpClient) {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
                log.info("🔌 Disconnected from the FTP server.");
            }
        } catch (IOException ex) {
            log.error("⚠️ Error while closing FTP connection: {}", ex.getMessage(), ex);
        }
    }
}