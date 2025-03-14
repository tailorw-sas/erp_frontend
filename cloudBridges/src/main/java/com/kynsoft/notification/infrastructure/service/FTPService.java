package com.kynsoft.notification.infrastructure.service;

import com.kynsof.share.core.application.ftp.FtpService;
import com.kynsoft.notification.domain.service.IFTPService;
import com.kynsoft.notification.infrastructure.config.FTPConfig;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

        try (inputStream) { // Ensures InputStream is closed automatically
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

            // Verify if the directory exists before attempting to change it
            if (!ftpClient.changeWorkingDirectory(remotePath)) {
                log.warn("⚠️ The directory '{}' does not exist on the FTP server. Attempting to create it...", remotePath);
                if (!ftpClient.makeDirectory(remotePath) || !ftpClient.changeWorkingDirectory(remotePath)) {
                    throw new RuntimeException("❌ Failed to create or access the directory on the FTP server.");
                }
                log.info("✅ Successfully created directory '{}'.", remotePath);
            }

            // Upload the file
            if (ftpClient.storeFile(fileName, inputStream)) {
                log.info("✅ File '{}' successfully uploaded to '{}'.", fileName, remotePath);
            } else {
                throw new RuntimeException("❌ Failed to upload the file to the FTP server.");
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