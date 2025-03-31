//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kynsof.share.core.domain.service;

import com.kynsof.share.core.domain.request.FileRequest;
import com.kynsof.share.core.domain.response.FileDto;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface IAmazonClient {
    String getBucketName();

    void setBucketName(String bucketName);

    void uploadFile(InputStream streamToUpload, String contentType, String objectKey) throws IOException;

    String save(FileRequest fileRequest) throws IOException;

    Mono<String> save(FilePart filePart);

    String save(byte[] bytes, String fileName, String contentType) throws IOException;

    List<FileDto> saveAll(List<FileRequest> files);

    void delete(String url);

    byte[] downloadFile(String filePath);
}
