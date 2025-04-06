package com.kynsoft.notification.controller;

import com.kynsof.share.core.domain.response.ApiError;
import com.kynsof.share.core.domain.response.ApiResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.notification.application.command.file.confirm.ConfirmFileCommand;
import com.kynsoft.notification.application.command.file.confirm.ConfirmFileMessage;
import com.kynsoft.notification.application.command.file.confirm.ConfirmFileRequest;
import com.kynsoft.notification.application.command.file.saveFileS3.SaveFileS3Command;
import com.kynsoft.notification.application.command.file.saveFileS3.SaveFileS3Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private final IMediator mediator;

    @Autowired
    public FileController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping(value = "")
    public Mono<ResponseEntity<ApiResponse<SaveFileS3Message>>> upload(@RequestPart("file") FilePart filePart) {
        return Mono.fromCallable(() -> {
            SaveFileS3Message response = this.mediator.send(new SaveFileS3Command(filePart));
            return ResponseEntity.ok(ApiResponse.success(response));
        }).subscribeOn(Schedulers.boundedElastic()).onErrorResume((e) -> {
            log.error("❌ Error uploading file: {}", e.getMessage(), e);
            return Mono.just(ResponseEntity.internalServerError().body(ApiResponse.fail(new ApiError("Failed to upload file: " + e.getMessage()))));
        });
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> loadFile(@RequestBody ConfirmFileRequest request) {
        ConfirmFileCommand command = ConfirmFileCommand.fromRequest(request);
        ConfirmFileMessage response = mediator.send(command);
        return ResponseEntity.ok(ApiResponse.success(response));

    }
}


