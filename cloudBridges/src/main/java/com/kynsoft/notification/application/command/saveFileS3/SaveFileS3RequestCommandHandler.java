package com.kynsoft.notification.application.command.saveFileS3;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.notification.domain.dto.AFileDto;
import com.kynsoft.notification.domain.service.IAFileService;
import com.kynsoft.notification.domain.service.IAmazonClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class SaveFileS3RequestCommandHandler implements ICommandHandler<SaveFileS3RequestCommand> {

    private final IAmazonClient amazonClient;
    private final IAFileService fileService;


    public SaveFileS3RequestCommandHandler(IAmazonClient amazonClient, IAFileService fileService) {

        this.amazonClient = amazonClient;
        this.fileService = fileService;
    }

    @Override
    public void handle(SaveFileS3RequestCommand command) {
        try {
            String url = amazonClient.save(command.getMultipartFile(), command.getFonder());
            AFileDto aFileDto = new AFileDto(UUID.randomUUID(),command.getMultipartFile().getName(), "file", url);
            UUID fileId = fileService.create(aFileDto);
            command.setFileId(fileId);
            command.setUrl(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
