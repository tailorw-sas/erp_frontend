package com.kynsoft.notification.application.command.file.saveFileS3;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.response.FileDto;
import com.kynsof.share.core.domain.service.IAmazonClient;
import com.kynsoft.notification.domain.service.IAFileService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class SaveFileS3CommandHandler implements ICommandHandler<SaveFileS3Command> {

    private final IAmazonClient amazonClient;
    private final IAFileService fileService;


    public SaveFileS3CommandHandler(@Qualifier("minio") IAmazonClient amazonClient, IAFileService fileService) {

        this.amazonClient = amazonClient;
        this.fileService = fileService;
    }

    @Override
    public void handle(SaveFileS3Command command) {
        try {
            String url = this.amazonClient.save(command.getFilePart()).block();
            if (url != null && !url.isBlank()) {
                FileDto aFileDto = new FileDto(UUID.randomUUID(), command.getFilePart().filename(), "file", url, false,
                        null, null);
                UUID fileId = this.fileService.create(aFileDto);
                if (fileId == null) {
                    throw new IOException("File ID was not generated, possible database failure.");
                } else {
                    command.setFileId(fileId);
                    command.setUrl(url);
                }
            } else {
                throw new IOException("MinIO returned an invalid URL for the uploaded file.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to process file upload", e);
        }
    }
}
