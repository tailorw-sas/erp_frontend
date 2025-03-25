package com.kynsoft.notification.domain.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface IFTPService {
    void uploadFile(String remotePath, byte[] fileBytes, String fileName, String server, String user, String password, int port) ;
    Mono<List<Map<String, String>>> uploadFilesBatch(String remotePath, List<FilePart> files, String server, String user, String password,
                                                     int port);
}