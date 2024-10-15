package com.kynsoft.notification.controller;

import com.kynsof.share.core.domain.service.IFtpService;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.notification.application.command.sendFtp.UploadFileCommand;
import com.kynsoft.notification.domain.service.IFTPService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@RestController
@RequestMapping("/api/ftp")
public class FTPController {
    private final IMediator mediator;
    private final IFTPService ftpService;

    public FTPController(IMediator mediator, IFTPService ftpService) {
        this.mediator = mediator;
        this.ftpService = ftpService;
    }

    @PostMapping("/upload")
    public Mono<ResponseEntity<String>> uploadFile(@RequestPart("file") FilePart file,
                                                   @RequestPart(value = "server") String server,
                                                   @RequestPart("user") String user,
                                                   @RequestPart("password") String password,
                                                   @RequestPart("port") String port,
                                                   @RequestPart("path") String path
                                                   ) {
        return Mono.fromRunnable(() -> mediator.send(new UploadFileCommand(file, server, user,password,port,path)))
                .then(Mono.just(ResponseEntity.ok("Archivo subido exitosamente.")))
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al subir el archivo: " + e.getMessage()));
                });
    }

    @GetMapping("/download")
    public Mono<ResponseEntity<byte[]>> downloadFile(@RequestParam("remoteFilePath") String remoteFilePath) {
        return Mono.fromCallable(() -> {
                    InputStream inputStream = ftpService.downloadFile(remoteFilePath, "162.55.193.5",
                            "usrftp01", "usuarioftp01*", 21);

                    if (inputStream == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new byte[0]);
                    }

                    // Convertir InputStream a byte[]
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, bytesRead);
                    }

                    byte[] fileBytes = buffer.toByteArray();

                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + remoteFilePath);

                    return ResponseEntity.ok()
                            .headers(headers)
                            .contentLength(fileBytes.length)
                            .body(fileBytes);
                })
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new byte[0]));
                });
    }
}