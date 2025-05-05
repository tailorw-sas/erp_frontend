package com.kynsoft.notification.controller;

import com.kynsof.share.core.application.FileRequest;
import com.kynsoft.notification.domain.service.IFTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ftp")
public class FTPController {
    private final IFTPService ftpService;
    private static final Logger log = LoggerFactory.getLogger(FTPController.class);

    public FTPController(IFTPService ftpService) {
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

        return file.content()
                .collectList()
                .flatMap(dataBuffers -> {
                    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        for (var buffer : dataBuffers) {
                            buffer.asInputStream(true).transferTo(outputStream);
                        }
                        byte[] fileBytes = outputStream.toByteArray();

                        if (fileBytes.length == 0) {
                            return Mono.error(new RuntimeException("❌ File content is empty."));
                        }

                        return Mono.fromCallable(() -> {
                            ftpService.uploadFile(path, fileBytes, fileName, server, user, password, Integer.parseInt(port));
                            return ResponseEntity.ok(Map.of(
                                    "message", "Upload successful",
                                    "file", fileName,
                                    "path", path != null ? path : ""
                            ));
                        }).subscribeOn(Schedulers.boundedElastic());
                    } catch (Exception e) {
                        log.error("❌ Error processing file '{}': {}", fileName, e.getMessage(), e);
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "Upload failed", "details", e.getMessage())));
                    }
                });
    }
}