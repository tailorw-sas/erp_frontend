package com.kynsoft.notification.controller;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.notification.application.command.sendFtp.UploadFileCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ftp")
public class FTPController {
    private final IMediator mediator;

    public FTPController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/upload")
    public Mono<ResponseEntity<String>> uploadFile(@RequestPart("file") FilePart file,
                                                   @RequestPart(value = "server") String server,
                                                   @RequestPart("user") String user,
                                                   @RequestPart("password") String password,
                                                   @RequestPart("port") String port,
                                                   @RequestPart("path") String path
                                                   ) {
        // Enviar el comando al bus de comandos para que lo maneje el handler correspondiente
        return Mono.fromRunnable(() -> mediator.send(new UploadFileCommand(file, server, user,password,port,path)))
                .then(Mono.just(ResponseEntity.ok("Archivo subido exitosamente.")))
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al subir el archivo: " + e.getMessage()));
                });
    }
}