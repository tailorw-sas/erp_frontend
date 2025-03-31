package com.kynsof.share.core.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private UUID id;
    private String name;
    private String microServiceName;
    private String url;
    private boolean isConfirmed;
    @JsonIgnore
    protected UploadFileResponse uploadFileResponse;
    @JsonIgnore
    protected byte[] fileContent;
}
