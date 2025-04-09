package com.kynsoft.notification.application.command.sendByFtp;

import com.kynsof.share.core.domain.response.FileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendByFtpRequest {
    private String server;
    private String userName;
    private String password;
    private String url;
    private String bucketName;
    private List<FileDto> fileDtos;
}
