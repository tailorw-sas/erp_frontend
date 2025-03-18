package com.kynsoft.notification.application.command.sendFtp;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.notification.domain.service.IFTPService;
import org.springframework.stereotype.Component;

@Component
public class UploadFileCommandHandler implements ICommandHandler<UploadFileCommand> {
    private final IFTPService ftpService;

    public UploadFileCommandHandler(IFTPService ftpService) {
        this.ftpService = ftpService;
    }

    @Override
    public void handle(UploadFileCommand command) {
        try {
            ftpService.uploadFile(command.getPath(), command.getFileBytes(), command.getFileName(), command.getServer(), command.getUser(),
                    command.getPassword(), command.getPort());
        } catch (Exception e) {
            throw new RuntimeException("FTP upload error: " + e.getMessage(), e);
        }
    }
}