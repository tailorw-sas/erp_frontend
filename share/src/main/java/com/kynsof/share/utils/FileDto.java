package com.kynsof.share.utils;

import com.kynsof.share.core.domain.response.UploadFileResponse;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FileDto {
    protected UUID id;
    protected String name;
    protected String originalName;
    protected byte[] file;
    protected String fileUrl;
    protected long size;
    protected MediaType mediaType;
    protected UploadFileResponse uploadFileResponse;


    public FileDto(String name, String fileUrl) {
        this.name = name;
        this.fileUrl = fileUrl;
    }

    public FileDto(String originalName, byte[] file) {
        this.originalName = originalName;
        this.file = file;
        this.size = file.length;
    }

    public FileDto(String originalName, UploadFileResponse uploadFileResponse ) {
        this.originalName = originalName;
        this.uploadFileResponse = uploadFileResponse;
    }

    public FileDto(String originalName, String fileUrl, UploadFileResponse uploadFileResponse ) {
        this.originalName = originalName;
        this.fileUrl = fileUrl;
        this.uploadFileResponse = uploadFileResponse;
    }
}