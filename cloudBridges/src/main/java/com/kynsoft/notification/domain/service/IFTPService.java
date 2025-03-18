package com.kynsoft.notification.domain.service;
import java.io.InputStream;

public interface IFTPService {
    void uploadFile(String remotePath, byte[] fileBytes, String fileName, String server, String user,
                    String password, int port) ;

    InputStream downloadFile(String remoteFilePath, String server, String user, String password, int port);
}