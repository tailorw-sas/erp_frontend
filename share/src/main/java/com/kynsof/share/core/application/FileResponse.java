package com.kynsof.share.core.application;

import com.kynsof.share.core.domain.bus.query.IResponse;

import java.time.LocalDateTime;

public class FileResponse implements IResponse {

    private final LocalDateTime time;

    private final byte[] document;


    public FileResponse(LocalDateTime time, byte[] document) {
        this.time = time;
        this.document = document;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public byte[] getDocument() {
        return document;
    }
}
