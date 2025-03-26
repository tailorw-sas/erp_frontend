package com.kynsof.share.core.domain.service;

import com.kynsof.share.utils.FileDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IAmazonClient {

    void uploadFile(InputStream streamToUpload, Long size, String contentType, String objectKey) throws IOException;

    String save(FileDto file) throws IOException;
    List<FileDto> saveAll(List<FileDto> files);

    void delete(String url);

    FileDto loadFile(String url);
}
