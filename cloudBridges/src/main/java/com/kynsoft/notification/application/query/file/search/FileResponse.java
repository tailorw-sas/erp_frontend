package com.kynsoft.notification.application.query.file.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsof.share.core.domain.response.FileDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class FileResponse implements IResponse {
    private UUID id;
    private String name;
    private String microServiceName;
    private String url;

    public FileResponse(FileDto fileDto) {
        this.id = fileDto.getId();
        this.name = fileDto.getName();
        this.microServiceName = fileDto.getMicroServiceName();
        this.url = fileDto.getUrl();
    }

}