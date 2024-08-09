package com.kynsoft.finamer.payment.infrastructure.excel.remote;

import lombok.Data;

import java.util.UUID;

@Data
public class SaveFileS3Message {

    private final String url;
    private final UUID id;



    public SaveFileS3Message(String result, UUID id) {
        this.url = result;
        this.id = id;
    }
}
