package com.kynsoft.notification.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileInfoDto {
    private String key;
    private String url;
    public FileInfoDto(String key, String cloudfrontDomain) {
        this.key = key;
        this.url = cloudfrontDomain + key;
    }
}
