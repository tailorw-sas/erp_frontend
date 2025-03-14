package com.kynsoft.notification.application.command.sendFtp;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.notification.domain.service.IFTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;

@Component
public class UploadFileCommandHandler implements ICommandHandler<UploadFileCommand> {

    private static final Logger log = LoggerFactory.getLogger(UploadFileCommandHandler.class);
    private final IFTPService ftpService;

    public UploadFileCommandHandler(IFTPService ftpService) {
        this.ftpService = ftpService;
    }

    @Override
    public void handle(UploadFileCommand command) {
        command.getFile().content()
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(dataBuffer -> {
                    try (InputStream inputStream = dataBuffer.asInputStream()) {
                        String remotePath = "/" + command.getPath();
                        log.info("📤 Uploading file '{}' to FTP at '{}'", command.getFile().filename(), remotePath);

                        return Mono.fromRunnable(() -> {
                            try {
                                ftpService.uploadFile(remotePath, inputStream, command.getFile().filename(),
                                        command.getServer(), command.getUser(), command.getPassword(), command.getPort());
                            } catch (Exception e) {
                                log.error("❌ FTP upload failed for '{}': {}", command.getFile().filename(), e.getMessage(), e);
                                throw new RuntimeException("FTP upload error: " + e.getMessage(), e);
                            }
                        }).subscribeOn(Schedulers.boundedElastic());
                    } catch (Exception e) {
                        log.error("❌ Error processing file '{}': {}", command.getFile().filename(), e.getMessage(), e);
                        return Mono.error(new RuntimeException("File processing error: " + e.getMessage(), e));
                    }
                })
                .subscribe();
    }
}