package com.kynsoft.notification.application.command.sendFtp;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.notification.domain.service.IFTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;

@Component
public class UploadFileCommandHandler implements ICommandHandler<UploadFileCommand> {

    private static final Logger log = LoggerFactory.getLogger(UploadFileCommandHandler.class);
    private final IFTPService ftpService;

    public UploadFileCommandHandler(IFTPService ftpService) {
        this.ftpService = ftpService;
    }

    @Override
    public void handle(UploadFileCommand command) {
        String remotePath = (command.getPath() == null || command.getPath().trim().isEmpty()) ? "" : "/" + command.getPath();
        log.info("📤 Uploading file '{}' to FTP at '{}'", command.getFileName(), remotePath);

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(command.getFileBytes())) {
            ftpService.uploadFile(remotePath, byteArrayInputStream, command.getFileName(),
                    command.getServer(), command.getUser(), command.getPassword(), command.getPort());
            log.info("✅ File '{}' successfully uploaded to FTP '{}'", command.getFileName(), remotePath);
        } catch (Exception e) {
            log.error("❌ FTP upload failed for '{}': {}", command.getFileName(), e.getMessage(), e);
            throw new RuntimeException("FTP upload error: " + e.getMessage(), e);
        }
    }
}