package com.kynsoft.notification.controller;

import com.kynsof.share.core.domain.response.ApiResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.notification.application.command.file.confirm.ConfirmFileCommand;
import com.kynsoft.notification.application.command.file.confirm.ConfirmFileMessage;
import com.kynsoft.notification.application.command.file.confirm.ConfirmFileRequest;
import com.kynsoft.notification.application.command.file.saveFileS3.SaveFileS3Command;
import com.kynsoft.notification.application.command.file.saveFileS3.SaveFileS3Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final IMediator mediator;

    @Autowired
    public FileController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping(value = "")
    public Mono<ResponseEntity<ApiResponse<?>>> upload(@RequestPart("file") FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    MultipartFile multipartFile = new MockMultipartFile("file",
                            filePart.filename(), Objects.requireNonNull(filePart.headers().getContentType()).toString(), bytes);

                    try {
                        SaveFileS3Message response = mediator.send(new SaveFileS3Command(multipartFile, "finamer"));
                        return Mono.just(ResponseEntity.ok(ApiResponse.success(response)));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });

    }

    @PostMapping("/confirm")
    public ResponseEntity<?> loadFile(@RequestBody ConfirmFileRequest request) {
        ConfirmFileCommand command = ConfirmFileCommand.fromRequest(request);
        ConfirmFileMessage response = mediator.send(command);
        return ResponseEntity.ok(ApiResponse.success(response));

    }
}


