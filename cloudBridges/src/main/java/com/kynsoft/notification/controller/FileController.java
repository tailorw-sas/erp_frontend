package com.kynsoft.notification.controller;

import com.kynsof.share.core.application.FileRequest;
import com.kynsof.share.core.domain.response.ApiResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsof.share.core.infrastructure.util.CustomMultipartFile;
import com.kynsoft.notification.application.command.saveFileS3.SaveFileS3RequestCommand;
import com.kynsoft.notification.application.command.saveFileS3.SaveFileS3RequestMessage;
import com.kynsoft.notification.domain.dto.AFileDto;
import com.kynsoft.notification.domain.dto.FileInfoDto;
import com.kynsoft.notification.domain.service.IAFileService;
import com.kynsoft.notification.infrastructure.service.AmazonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final IMediator mediator;

    private final AmazonClient amazonClient;

    private final IAFileService fileService;

    @Autowired
    public FileController(IMediator mediator, AmazonClient amazonClient, IAFileService fileService) {
        this.mediator = mediator;
        this.amazonClient = amazonClient;
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestBody FileRequest request) {
        try {
            MultipartFile file = new CustomMultipartFile(request.getFile(), request.getFileName());
            String fileUrl = amazonClient.save(file, request.getFileName());
            this.fileService.create(new AFileDto(UUID.randomUUID(), request.getFileName(), "", fileUrl));
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload file: " + e.getMessage());
        }
    }

    @PostMapping(value = "")
    public Mono<ResponseEntity<ApiResponse<?>>> upload(@RequestPart("file") FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    // Crear MultipartFile a partir de bytes
                    MultipartFile multipartFile = new MockMultipartFile("file",
                            filePart.filename(), Objects.requireNonNull(filePart.headers().getContentType()).toString(), bytes);

                    try {
                        // Llamar a AmazonClient para guardar el archivo
                        SaveFileS3RequestMessage response = mediator.send(new SaveFileS3RequestCommand(multipartFile, "medinec"));
                        return Mono.just(ResponseEntity.ok(ApiResponse.success(response)));
                    } catch (Exception e) {
                        //return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload: " + e.getMessage()));
                        return Mono.error(e);
                    }
                });

    }


    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("url") String url) {
        try {
            amazonClient.delete(url);
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete file: " + e.getMessage());
        }
    }

    @GetMapping("/load")
    public ResponseEntity<AFileDto> loadFile(@RequestParam("url") String fileUrl) {
        try {
            AFileDto file = amazonClient.loadFile(fileUrl);
            return ResponseEntity.ok(file);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/load/by/id")
    public ResponseEntity<AFileDto> loadFile(@RequestParam("id") UUID id) {

        try {
            AFileDto fileSave = this.fileService.findById(id);

            AFileDto file = amazonClient.loadFile(fileSave.getUrl());
            return ResponseEntity.ok(file);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping()
    public List<FileInfoDto> listFiles(@RequestParam("bucketName") String bucketName) {
        return amazonClient.listAllFiles(bucketName);
    }

    @GetMapping("/delete/key")
    public String deleteFileByKey( @RequestParam("key") String key) {

            amazonClient.deleteFileByKey(key);
            return "Archivo eliminado con éxito.";

    }

    @GetMapping("/buckets")
    public List<String> listBuckets() {
        return amazonClient.listAllBuckets();
    }
}


