package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FileKafka {

    private UUID id;
    private String microServiceName;
    private String fileName;
    private byte [] file;

    public FileKafka() {
    }

}
