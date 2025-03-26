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

//    @PostMapping("/upload-multiple")
//    public Mono<ResponseEntity<Map<String, Object>>> uploadMultipleFiles(@RequestPart("files") Flux<FilePart> files,
//                                                                         @RequestPart("server") String server,
//                                                                         @RequestPart("user") String user,
//                                                                         @RequestPart("password") String password,
//                                                                         @RequestPart("port") String portStr,
//                                                                         @RequestPart(value = "path", required = false) String path) {
//        // Convert port string to integer with validation
//        int portNumber;
//        try {
//            portNumber = Integer.parseInt(portStr);
//            if (portNumber <= 0 || portNumber > 65535) {
//                return Mono.just(ResponseEntity.badRequest()
//                        .body(Map.of("message", "Invalid port number. Must be between 1-65535.")));
//            }
//        } catch (NumberFormatException e) {
//            return Mono.just(ResponseEntity.badRequest()
//                    .body(Map.of("message", "Invalid port format. Must be a valid number.")));
//        }
//
//        final int validPortNumber = portNumber;
//
//        return files.collectList()
//                .flatMap(fileParts -> {
//                    if (fileParts.isEmpty()) {
//                        return Mono.just(ResponseEntity.badRequest()
//                                .body(Map.of("message", "No files received for upload.")));
//                    }
//
//                    // Corrección aquí: Manejo de la respuesta asíncrona
//                    return ftpService.uploadFilesBatch(path, fileParts, server, user, password, validPortNumber)
//                            .map(uploadResults -> {
//                                List<Map<String, Object>> successfulUploads = uploadResults.stream()
//                                        .filter(result -> "success".equals(result.get("status")))
//                                        .map(HashMap::new) // Convertimos de Map<String, String> a Map<String, Object>
//                                        .collect(Collectors.toList());
//
//                                List<Map<String, Object>> failedUploads = uploadResults.stream()
//                                        .filter(result -> "failed".equals(result.get("status")))
//                                        .map(HashMap::new)
//                                        .collect(Collectors.toList());
//
//                                Map<String, Object> response = new HashMap<>();
//                                response.put("message", "Batch upload completed");
//                                response.put("successfulUploads", successfulUploads);
//                                response.put("failedUploads", failedUploads);
//                                response.put("totalSuccessful", successfulUploads.size());
//                                response.put("totalFailed", failedUploads.size());
//
//                                return ResponseEntity.ok(response);
//                            })
//                            .onErrorResume(ex -> {
//                                log.error("❌ Error uploading multiple files: {}", ex.getMessage(), ex);
//                                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                                        .body(Map.of("error", "Batch upload failed", "details", ex.getMessage())));
//                            });
//                });
//    }
}