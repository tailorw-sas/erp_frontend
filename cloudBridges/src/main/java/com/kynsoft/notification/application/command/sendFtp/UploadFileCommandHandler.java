package com.kynsoft.notification.application.command.sendFtp;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.notification.domain.service.IFTPService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;

@Component
public class UploadFileCommandHandler implements ICommandHandler<UploadFileCommand> {

    private final IFTPService ftpService;

    public UploadFileCommandHandler(IFTPService ftpService) {
        this.ftpService = ftpService;
    }

    @Override
    public void handle(UploadFileCommand command) {
        command.getFile().content()
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(dataBuffer -> {
                    InputStream inputStream = dataBuffer.asInputStream();
                    String remotePath = "/files/" + command.getFile().filename();

                    return Mono.fromRunnable(() -> {
                        try {
                            ftpService.uploadFile(remotePath, inputStream, command.getFile().filename());
                            System.out.println("Archivo subido exitosamente al FTP.");
                        } catch (Exception e) {
                            throw new RuntimeException("Error durante la operación FTP: " + e.getMessage(), e);
                        } finally {
                            try {
                                inputStream.close();
                            } catch (Exception e) {
                                System.err.println("Error al cerrar el InputStream: " + e.getMessage());
                            }
                        }
                    }).subscribeOn(Schedulers.boundedElastic());
                })
                .subscribe();
    }
}