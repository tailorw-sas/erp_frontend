package com.kynsof.share.core.domain.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileRequest {
    private UUID objectId;
    private String fileName;
    private byte[] file;
    private String contentType;
}
