//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kynsoft.notification.domain.service;

import com.kynsof.share.core.domain.response.FileDto;
import java.util.List;
import reactor.core.publisher.Mono;

public interface IFTPService {
    void uploadFile(String remotePath, byte[] fileBytes, String fileName, String server, String user, String password, int port);

    Mono<Void> uploadFilesBatch(String remotePath, List<FileDto> files, String server, String user, String password);
}
