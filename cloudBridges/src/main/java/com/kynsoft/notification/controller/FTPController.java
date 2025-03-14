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
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/ftp")
public class FTPController {
    private final IMediator mediator;
    private final IFTPService ftpService;
    private static final Logger log = LoggerFactory.getLogger(FTPController.class);

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

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

        return Mono.fromFuture(() -> CompletableFuture.supplyAsync(() -> {
            try {
                mediator.send(new UploadFileCommand(file, server, user, password, port, path));
                log.info("✅ File '{}' successfully uploaded to FTP", fileName);
                return ResponseEntity.ok(Map.of("message", "Upload successful", "file", fileName, "path", path));
            } catch (Exception e) {
                log.error("❌ Error uploading file '{}': {}", fileName, e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Upload failed", "details", e.getMessage()));
            }
        }, executorService));
    }

    @GetMapping("/download")
    public Mono<ResponseEntity<byte[]>> downloadFile(@RequestParam("remoteFilePath") String remoteFilePath,
                                                     @RequestParam("server") String server,
                                                     @RequestParam("user") String user,
                                                     @RequestParam("password") String password,
                                                     @RequestParam("port") int port) {
        log.info("📥 Downloading file '{}' from FTP server...", remoteFilePath);

        return Mono.fromFuture(() -> CompletableFuture.supplyAsync(() -> {
            try (InputStream inputStream = ftpService.downloadFile(remoteFilePath, server, user, password, port)) {
                if (inputStream == null) {
                    log.warn("⚠️ File '{}' not found on FTP server.", remoteFilePath);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new byte[0]);
                }

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[4096]; // Increased buffer size for efficiency
                int bytesRead;
                while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }

                byte[] fileBytes = buffer.toByteArray();
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + remoteFilePath);

                log.info("✅ Successfully downloaded file '{}'", remoteFilePath);
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(fileBytes.length)
                        .body(fileBytes);
            } catch (Exception e) {
                log.error("❌ Error downloading file '{}': {}", remoteFilePath, e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new byte[0]);
            }
        }, executorService));
    }
}