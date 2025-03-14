package com.kynsoft.notification.controller;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.notification.application.command.sendFtp.UploadFileCommand;
import com.kynsoft.notification.domain.service.IFTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

@RestController
@RequestMapping("/api/ftp")
public class FTPController {
    private final IMediator mediator;
    private final IFTPService ftpService;
    private static final Logger log = LoggerFactory.getLogger(FTPController.class);

    public FTPController(IMediator mediator, IFTPService ftpService) {
        this.mediator = mediator;
        this.ftpService = ftpService;
    }

    @PostMapping("/upload")
    public Mono<ResponseEntity<Map<String, String>>> uploadFile(@RequestPart("file") FilePart file,
                                                                @RequestPart("server") String server,
                                                                @RequestPart("user") String user,
                                                                @RequestPart("password") String password,
                                                                @RequestPart("port") String port,
                                                                @RequestPart(value = "path", required = false) String path) {
        String fileName = file.filename();
        log.info("📤 Received request to upload file '{}' to FTP '{}'", fileName, path);

        return file.content()
                .reduce(new ByteArrayOutputStream(), (outputStream, dataBuffer) -> {
                    try {
                        dataBuffer.asInputStream(true).transferTo(outputStream);
                    } catch (Exception e) {
                        log.error("❌ Error processing file '{}': {}", fileName, e.getMessage(), e);
                        throw new RuntimeException("Error processing file: " + e.getMessage(), e);
                    }
                    return outputStream;
                })
                .flatMap(outputStream -> {
                    byte[] fileBytes = outputStream.toByteArray();

                    if (fileBytes.length == 0) {
                        return Mono.error(new RuntimeException("❌ File content is empty."));
                    }

                    // Fix: Corrected parameter types for UploadFileCommand constructor
                    UploadFileCommand command = new UploadFileCommand(fileName, fileBytes, server, user, password, Integer.parseInt(port), path);
                    return Mono.fromCallable(() -> {
                        mediator.send(command);
                        return ResponseEntity.ok(Map.of("message", "Upload successful", "file", fileName, "path", path));
                    }).subscribeOn(Schedulers.boundedElastic());
                })
                .onErrorResume(e -> {
                    log.error("❌ Error uploading file '{}': {}", fileName, e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "Upload failed", "details", e.getMessage())));
                });
    }

    @PostMapping("/upload-multiple")
    public Mono<ResponseEntity<Map<String, Object>>> uploadMultipleFiles(@RequestPart("files") Flux<FilePart> files,
                                                                         @RequestPart("server") String server,
                                                                         @RequestPart("user") String user,
                                                                         @RequestPart("password") String password,
                                                                         @RequestPart("port") String port,
                                                                         @RequestPart(value = "path", required = false) String path) {
        log.info("📤 Received request to upload multiple files to FTP '{}'", path);

        return files.flatMap(file -> {
                    String fileName = file.filename();
                    log.info("📤 Processing upload for file '{}'", fileName);

                    return file.content()
                            .flatMap(dataBuffer -> {
                                try {
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    dataBuffer.asInputStream(true).transferTo(byteArrayOutputStream);
                                    byte[] fileBytes = byteArrayOutputStream.toByteArray();

                                    mediator.send(new UploadFileCommand(fileName, fileBytes, server, user, password, Integer.parseInt(port), path));
                                    log.info("✅ File '{}' successfully uploaded to FTP", fileName);
                                    return Mono.just(Map.of("file", fileName, "status", "success", "path", path));
                                } catch (Exception e) {
                                    log.error("❌ Error processing file '{}': {}", fileName, e.getMessage(), e);
                                    return Mono.just(Map.of("file", fileName, "status", "failed", "error", e.getMessage()));
                                }
                            })
                            .publishOn(Schedulers.boundedElastic());
                })
                .collectList()
                .map(results -> ResponseEntity.ok(Map.of("message", "Batch upload completed", "results", results)));
    }

    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> downloadFile(@RequestParam("remoteFilePath") String remoteFilePath,
                                                              @RequestParam("server") String server,
                                                              @RequestParam("user") String user,
                                                              @RequestParam("password") String password,
                                                              @RequestParam("port") int port) {
        log.info("📥 Initiating streaming download for file '{}' from FTP server...", remoteFilePath);

        InputStream inputStream;
        try {
            inputStream = ftpService.downloadFile(remoteFilePath, server, user, password, port);
            if (inputStream == null) {
                log.warn("⚠️ File '{}' not found on FTP server.", remoteFilePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            log.error("❌ Error initializing download for file '{}': {}", remoteFilePath, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + remoteFilePath);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);

        StreamingResponseBody responseBody = outputStream -> {
            byte[] buffer = new byte[8192]; // Use 8KB buffer for efficient streaming
            int bytesRead;
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush(); // Flush ensures data is sent in chunks
                }
            } catch (Exception e) {
                log.error("❌ Error while streaming file '{}': {}", remoteFilePath, e.getMessage(), e);
            } finally {
                inputStream.close();
            }
        };

        log.info("✅ Successfully initiated streaming for file '{}'", remoteFilePath);
        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);
    }
}