package com.kynsoft.notification.controller;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.notification.application.command.sendFtp.UploadFileCommand;
import com.kynsoft.notification.domain.service.IFTPService;
import com.kynsoft.notification.infrastructure.service.FTPService;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.UUID;

@RestController
@RequestMapping("/api/ftp")
public class FTPController {

//    @Autowired
//    private IFTPService ftpService;
    private final IMediator mediator;

    public FTPController(IMediator mediator) {
        this.mediator = mediator;
    }

//    @PostMapping("/upload")
//    public Mono<ResponseEntity<String>> uploadFile(@RequestPart("file") FilePart file) {
//
//        String remotePath = "/files/"+file.filename();
//        // Convertir el FilePart a un InputStream completo
//        return DataBufferUtils.join(file.content())
//                .map(dataBuffer -> dataBuffer.asInputStream(true))
//                .flatMap(inputStream -> Mono.fromRunnable(() -> {
//                    try {
//                        // Conectar al FTP
//                      //  ftpService.connect(server, port, user, password);
//                        System.out.println("Conexión exitosa al servidor FTP.");
//
//                        // Subir el archivo al FTP
//                        ftpService.uploadFile(remotePath, inputStream, file.filename());
//                        System.out.println("Archivo subido exitosamente al FTP.");
//                    } catch (Exception e) {
//                        throw new RuntimeException("Error durante la operación FTP: " + e.getMessage(), e);
//                    } finally {
//                        // Desconectar del FTP
//                     //   ftpService.disconnect();
//                        System.out.println("Desconectado del servidor FTP.");
//                    }
//                }).subscribeOn(Schedulers.boundedElastic())) // Ejecutar en un hilo separado
//                .then(Mono.just(ResponseEntity.ok("Archivo subido exitosamente.")))
//                .onErrorResume(e -> {
//                    e.printStackTrace();
//                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                            .body("Error al subir el archivo: " + e.getMessage()));
//                });
//
//    }

    @PostMapping("/upload")
    public Mono<ResponseEntity<String>> uploadFile(@RequestPart("file") FilePart file) {
        // Enviar el comando al bus de comandos para que lo maneje el handler correspondiente
        return Mono.fromRunnable(() -> mediator.send(new UploadFileCommand(file)))
                .then(Mono.just(ResponseEntity.ok("Archivo subido exitosamente.")))
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al subir el archivo: " + e.getMessage()));
                });
    }
}