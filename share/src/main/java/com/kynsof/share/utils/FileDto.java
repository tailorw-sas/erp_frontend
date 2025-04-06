package com.kynsof.share.utils;

import com.kynsof.share.core.domain.response.ResponseStatus;
import com.kynsof.share.core.domain.response.UploadFileResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
/*
@Getter
@Setter
public class FileDto {
    protected UUID id;
    protected String name;
    protected String originalName;
    protected String microServiceName;
    protected byte[] file;
    protected String fileUrl;
    protected long length;
    protected String mediaType;
    protected UploadFileResponse uploadFileResponse;
    protected boolean isConfirm;

    public FileDto(String name, String fileUrl) {
        this.name = name;
        this.fileUrl = fileUrl;
        this.uploadFileResponse = new UploadFileResponse(ResponseStatus.SUCCESS_RESPONSE);
    }

    public FileDto(UUID id, String name, String microServiceName, String fileUrl, boolean isConfirm) {
        this.id = id;
        this.name = name;
        this.originalName = name;
        this.microServiceName = microServiceName;
        this.fileUrl = fileUrl;
        this.isConfirm = isConfirm;
    }

    public FileDto(UUID id, String originalName, byte[] file, String mediaType, String microServiceName) {
        this.id = id;
        this.name = originalName;
        this.originalName = originalName;
        this.file = file;
        this.length = file.length;
        this.mediaType = mediaType;
        this.isConfirm = false;
        this.microServiceName = microServiceName;
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
}*/