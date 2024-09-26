package com.kynsoft.notification.infrastructure.service;

import com.kynsoft.notification.domain.service.IFTPService;
import com.kynsoft.notification.infrastructure.config.FTPConfig;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FTPService implements IFTPService {

    private final FTPConfig ftpConfig;

    public FTPService(FTPConfig ftpConfig) {
        this.ftpConfig = ftpConfig;
    }

    public void uploadFile(String remotePath, InputStream inputStream, String fileName, String server, String user,
                           String password, int port) {
        System.out.println("Inicio FTP");

        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, password);
            System.out.println("Conectado al FTP");

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setBufferSize(ftpConfig.getBufferSize());
            ftpClient.setConnectTimeout(ftpConfig.getConnectTimeout());
            ftpClient.setSoTimeout(ftpConfig.getSoTimeout());

            boolean success = ftpClient.storeFile(remotePath, inputStream);
            if (success) {
                System.out.println("El archivo se subió exitosamente.");
            } else {
                System.out.println("Error al subir el archivo.");
            }
            ftpClient.logout();
            System.out.println("Fin FTP");
        } catch (IOException ex) {
            System.out.println(ex.toString());
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException ex) {
                System.out.println("Error al desconectar: " + ex.getMessage());
            }
        }
    }
}