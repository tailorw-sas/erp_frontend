package com.kynsoft.notification.application.command.file.saveFileS3;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.service.IAmazonClient;
import com.kynsof.share.utils.FileDto;
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
            String url = amazonClient.save(command.getMultipartFile());
            FileDto aFileDto = new FileDto(UUID.randomUUID(),command.getMultipartFile().getName(), "file", url, false);
            UUID fileId = fileService.create(aFileDto);
            command.setFileId(fileId);
            command.setUrl(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
