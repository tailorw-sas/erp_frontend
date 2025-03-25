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

    @PostMapping("/upload-multiple")
    public Mono<ResponseEntity<Map<String, Object>>> uploadMultipleFiles(@RequestPart("files") Flux<FilePart> files,
                                                                         @RequestPart("server") String server,
                                                                         @RequestPart("user") String user,
                                                                         @RequestPart("password") String password,
                                                                         @RequestPart("port") String portStr,
                                                                         @RequestPart(value = "path", required = false) String path) {
        // Convert port string to integer with validation
        int portNumber;
        try {
            portNumber = Integer.parseInt(portStr);
            if (portNumber <= 0 || portNumber > 65535) {
                return Mono.just(ResponseEntity.badRequest()
                        .body(Map.of("message", "Invalid port number. Must be between 1-65535.")));
            }
        } catch (NumberFormatException e) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid port format. Must be a valid number.")));
        }

        // Store the port number as final for use in lambda
        final int validPortNumber = portNumber;

        return files
                .collectList()
                .flatMap(requests -> {
                    if (requests.isEmpty()) {
                        return Mono.just(ResponseEntity.badRequest()
                                .body(Map.of("message", "No files received for upload.")));
                    }

                    return Flux.fromIterable(requests)
                            .parallel()
                            .runOn(Schedulers.boundedElastic()) // Better for IO-bound operations than Schedulers.parallel()
                            .flatMap(fileRequest ->
                                    Mono.fromCallable(() -> {
                                                try {
                                                    // Here's the fix: ftpService returns List<Map<String, String>> but we need Map<String, Object>
                                                    List<Map<String, String>> resultList = ftpService.uploadFilesBatch(
                                                            path,
                                                            List.of(fileRequest),
                                                            server,
                                                            user,
                                                            password,
                                                            validPortNumber
                                                    );

                                                    // We need to get the first result from the list and convert it to Map<String, Object>
                                                    if (resultList == null || resultList.isEmpty() || !resultList.get(0).containsKey("status")) {
                                                        throw new RuntimeException("FTP service did not return a valid response.");
                                                    }

                                                    // Convert Map<String, String> to Map<String, Object>
                                                    Map<String, Object> objectMap = new HashMap<>();
                                                    resultList.get(0).forEach(objectMap::put);
                                                    return objectMap;
                                                } catch (Exception e) {
                                                    log.error("❌ Failed to upload file: {}", fileRequest.filename(), e);
                                                    Map<String, Object> errorResult = new HashMap<>();
                                                    errorResult.put("file", fileRequest.filename());
                                                    errorResult.put("status", "failed");
                                                    errorResult.put("error", e.getMessage());
                                                    return errorResult;
                                                }
                                            })
                                            .subscribeOn(Schedulers.boundedElastic())
                                            .retryWhen(reactor.util.retry.Retry.backoff(3, Duration.ofSeconds(2))
                                                    .maxBackoff(Duration.ofSeconds(10))
                                                    .filter(ex -> !(ex instanceof IllegalArgumentException)) // Don't retry invalid inputs
                                            )
                            )
                            .sequential()
                            .collectList()
                            .map(uploadResults -> {
                                List<Map<String, Object>> successfulUploads = uploadResults.stream()
                                        .filter(result -> "success".equals(result.get("status")))
                                        .collect(Collectors.toList());

                                List<Map<String, Object>> failedUploads = uploadResults.stream()
                                        .filter(result -> "failed".equals(result.get("status")))
                                        .collect(Collectors.toList());

                                Map<String, Object> response = new HashMap<>();
                                response.put("message", "Batch upload completed");
                                response.put("successfulUploads", successfulUploads);
                                response.put("failedUploads", failedUploads);
                                response.put("totalSuccessful", successfulUploads.size());
                                response.put("totalFailed", failedUploads.size());

                                return ResponseEntity.ok(response);
                            });
                });
    }
}