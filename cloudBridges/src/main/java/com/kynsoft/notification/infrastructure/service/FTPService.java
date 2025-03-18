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

    public void uploadFile(String remotePath, byte[] fileBytes, String fileName, String server, String user, String password, int port) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            if (!ftpClient.login(user, password)) {
                log.error("❌ Authentication failed with the FTP server.");
                throw new RuntimeException("Authentication failed with the FTP server.");
            }

            // Configuración del FTP
            configureFTPClient(ftpClient);

            // Validación del directorio remoto
            remotePath = validateAndSetRemotePath(ftpClient, remotePath);

            // Subir archivo usando byte array
            try (ByteArrayInputStream newInputStream = new ByteArrayInputStream(fileBytes)) {
                if (!ftpClient.storeFile(fileName, newInputStream)) {
                    log.error("❌ Failed to upload the file '{}' to {}.", fileName, remotePath);
                    throw new RuntimeException("FTP upload failed for file: " + fileName);
                }
            }
        } catch (IOException e) {
            log.error("❌ FTP connection error: {}", e.getMessage(), e);
            throw new RuntimeException("FTP connection error while uploading file: " + fileName, e);
        } finally {
            disconnectFTP(ftpClient);
        }
    }

    public InputStream downloadFile(String remoteFilePath, String server, String user, String password, int port) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            if (!ftpClient.login(user, password)) {
                log.error("❌ Authentication failed with the FTP server.");
                throw new RuntimeException("Authentication failed with the FTP server.");
            }

            configureFTPClient(ftpClient);
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFilePath);

            if (inputStream == null) {
                log.error("❌ Failed to retrieve file from FTP server.");
                throw new RuntimeException("Failed to retrieve file from FTP server.");
            }

            return new BufferedInputStream(inputStream);

        } catch (IOException e) {
            log.error("❌ FTP connection error: {}", e.getMessage(), e);
            throw new RuntimeException("FTP connection error while downloading file: " + remoteFilePath, e);
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
            return ftpClient.printWorkingDirectory();
        }

        if (!remotePath.equals(ftpClient.printWorkingDirectory())) {
            if (!ftpClient.changeWorkingDirectory(remotePath)) {
                if (!ftpClient.makeDirectory(remotePath) || !ftpClient.changeWorkingDirectory(remotePath)) {
                    throw new RuntimeException("❌ Failed to create/access the directory.");
                }
            }
        }
        return remotePath;
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
}