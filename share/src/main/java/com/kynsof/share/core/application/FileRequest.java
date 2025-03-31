package com.kynsof.share.core.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileRequest {

    public UUID objectId;
    private String fileName;
    private byte [] file;

}
