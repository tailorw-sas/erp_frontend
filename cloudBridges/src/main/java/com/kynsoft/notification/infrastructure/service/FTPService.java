package com.kynsoft.notification.infrastructure.service;

import com.kynsof.share.core.application.ftp.FtpService;
import com.kynsoft.notification.domain.service.IFTPService;
import com.kynsoft.notification.infrastructure.config.FTPConfig;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
        try {
            log.info("🔗 Conectando al servidor FTP: {} en el puerto {}", server, port);
            ftpClient.connect(server, port);
            log.info("🔗 Logueandose con las credenciales siguientes: user {}, pass {}", user, password);
            boolean login = ftpClient.login(user, password);
            if (!login) {
                throw new RuntimeException("❌ Falló la autenticación con el servidor FTP.");
            }

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setBufferSize(ftpConfig.getBufferSize());
            ftpClient.setConnectTimeout(ftpConfig.getConnectTimeout());
            ftpClient.setSoTimeout(ftpConfig.getSoTimeout());

            if (!ftpClient.changeWorkingDirectory(remotePath)) {
                log.warn("⚠️ El directorio {} no existe en el servidor FTP. Creando...", remotePath);
                if (!ftpClient.makeDirectory(remotePath)) {
                    throw new RuntimeException("❌ No se pudo crear el directorio en el servidor FTP.");
                }
                ftpClient.changeWorkingDirectory(remotePath);
            }

            boolean uploaded = ftpClient.storeFile(fileName, inputStream);
            if (uploaded) {
                log.info("✅ Archivo '{}' subido correctamente al servidor FTP en '{}'.", fileName, remotePath);
            } else {
                throw new RuntimeException("❌ No se pudo subir el archivo al servidor FTP.");
            }
        } catch (IOException e) {
            log.error("❌ Error de conexión con el servidor FTP: {}", e.getMessage(), e);
            throw new RuntimeException("Error en la conexión FTP: " + e.getMessage(), e);
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                    log.info("🔌 Desconectado del servidor FTP.");
                }
            } catch (IOException ex) {
                log.error("⚠️ Error al cerrar la conexión FTP: {}", ex.getMessage(), ex);
            }
        }
    }

    // Método para descargar un archivo
    public InputStream downloadFile(String remoteFilePath)  {
        FTPClient ftpClient = new FTPClient();
        InputStream inputStream = null;
//        try {
//            ftpClient.connect(ftpServerAddress, ftpServerPort);
//            ftpClient.login(ftpUsername, ftpPassword);
//            ftpClient.enterLocalPassiveMode();
//            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//            ftpClient.setBufferSize(ftpConfig.getBufferSize());
//            ftpClient.setConnectTimeout(ftpConfig.getConnectTimeout());
//            ftpClient.setSoTimeout(ftpConfig.getSoTimeout());
//
//            // Descargar el archivo
//            inputStream = ftpClient.retrieveFileStream(remoteFilePath);
//            if (inputStream == null) {
//                throw new IOException("No se pudo descargar el archivo desde: " + remoteFilePath);
//            }
//
//            System.out.println("Archivo descargado desde el FTP: " + remoteFilePath);
//            ftpClient.logout();
//        } catch (IOException ex) {
//            System.out.println("Error al descargar el archivo: " + ex.getMessage());
//
//        } finally {
//            try {
//                if (ftpClient.isConnected()) {
//                    ftpClient.disconnect();
//                }
//            } catch (IOException ex) {
//                System.out.println("Error al desconectar: " + ex.getMessage());
//            }
//        }
        return inputStream; // Devuelve el InputStream para que el archivo pueda ser leído
    }
}