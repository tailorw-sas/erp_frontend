package com.kynsoft.notification.application.command.sendByFtp;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.domain.response.FileDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@Builder
public class SendByFtpCommand implements ICommand {
    private final String server;
    private final String userName;
    private final String password;
    private final String url;
    private final String bucketName;
    private final List<FileDto> fileDtos;

    public static SendByFtpCommand fromRequest(SendByFtpRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");
        return SendByFtpCommand.builder()
                .server(request.getServer())
                .userName(request.getUserName())
                .password(request.getPassword())
                .url(request.getUrl())
                .bucketName(request.getBucketName())
                .fileDtos(request.getFileDtos())
                .build();
    }

    public ICommandMessage getMessage() {
        return new SendByFtpMessage();
    }
}
