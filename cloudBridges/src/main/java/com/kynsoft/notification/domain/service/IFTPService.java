package com.kynsoft.notification.domain.service;
import java.io.InputStream;

public interface IFTPService {
    void uploadFile(String remotePath, InputStream inputStream, String fileName, String server, String user,
                    String password, int port) ;
}