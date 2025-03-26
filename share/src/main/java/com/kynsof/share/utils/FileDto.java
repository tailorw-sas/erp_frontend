package com.kynsof.share.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {
    protected UUID id;
    protected String name;
    protected String microServiceName;
    protected String url;
    protected boolean isConfirm;

    public FileDto(String name, String url) {
        this.name = name;
        this.url = url;
    }
}