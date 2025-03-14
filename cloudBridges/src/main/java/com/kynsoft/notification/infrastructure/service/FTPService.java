package com.kynsoft.notification.infrastructure.service;

import com.kynsof.share.core.application.ftp.FtpService;
import com.kynsoft.notification.domain.service.IFTPService;
import com.kynsoft.notification.infrastructure.config.FTPConfig;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

            // Connection settings
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setBufferSize(ftpConfig.getBufferSize());
            ftpClient.setConnectTimeout(ftpConfig.getConnectTimeout());
            ftpClient.setSoTimeout(ftpConfig.getSoTimeout());

            // Only attempt to change directory if remotePath is provided
            if (remotePath != null && !remotePath.trim().isEmpty() && !"/".equals(remotePath)) {
                log.info("📂 Changing to directory: {}", remotePath);

                if (!ftpClient.changeWorkingDirectory(remotePath)) {
                    log.warn("⚠️ Directory '{}' does not exist. Creating it...", remotePath);

                    if (!ftpClient.makeDirectory(remotePath) || !ftpClient.changeWorkingDirectory(remotePath)) {
                        throw new RuntimeException("❌ Failed to create/access the directory.");
                    }

                    log.info("✅ Successfully created and accessed '{}'.", remotePath);
                }
            } else {
                log.info("📂 No remotePath provided, using default FTP directory.");
            }

            // Convert InputStream to ByteArrayInputStream to prevent premature closing
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            inputStream.transferTo(byteArrayOutputStream);
            InputStream newInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            // Upload the file
            if (ftpClient.storeFile(fileName, newInputStream)) {
                log.info("✅ File '{}' successfully uploaded to '{}'.", fileName, remotePath);
            } else {
                throw new RuntimeException("❌ Failed to upload the file.");
            }

        } catch (IOException e) {
            log.error("❌ FTP connection error: {}", e.getMessage(), e);
            throw new RuntimeException("FTP connection error: " + e.getMessage(), e);
        } finally {
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

    // Method to download a file
    public InputStream downloadFile(String remoteFilePath, String server, String user, String password, int port) {
        FTPClient ftpClient = new FTPClient();

        try {
            log.info("🔗 Connecting to FTP server: {} on port {}", server, port);
            ftpClient.connect(server, port);

            if (!ftpClient.login(user, password)) {
                throw new RuntimeException("❌ Authentication failed with the FTP server.");
            }

            log.info("✅ Successfully authenticated with the FTP server.");
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setBufferSize(ftpConfig.getBufferSize());
            ftpClient.setConnectTimeout(ftpConfig.getConnectTimeout());
            ftpClient.setSoTimeout(ftpConfig.getSoTimeout());

            log.info("📂 Downloading file: {}", remoteFilePath);
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFilePath);

            if (inputStream == null) {
                throw new RuntimeException("❌ Failed to retrieve file from FTP server.");
            }

            log.info("✅ File '{}' successfully retrieved from FTP server.", remoteFilePath);
            return inputStream;

        } catch (IOException e) {
            log.error("❌ FTP connection error: {}", e.getMessage(), e);
            throw new RuntimeException("FTP connection error: " + e.getMessage(), e);
        } finally {
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
}