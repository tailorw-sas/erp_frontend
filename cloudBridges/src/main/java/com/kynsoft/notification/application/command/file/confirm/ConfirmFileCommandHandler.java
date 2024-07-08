package com.kynsoft.notification.application.command.file.confirm;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.notification.domain.dto.AFileDto;
import com.kynsoft.notification.domain.dto.FileInfoDto;
import com.kynsoft.notification.domain.service.IAFileService;
import com.kynsoft.notification.domain.service.IAmazonClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class ConfirmFileCommandHandler implements ICommandHandler<ConfirmFileCommand> {


    private final IAFileService fileService;


    public ConfirmFileCommandHandler( IAFileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public void handle(ConfirmFileCommand command) {
        List<AFileDto> list = fileService.findByIds(command.getIds());
        if (!list.isEmpty()) {
            list.stream().map(aFileDto -> {
                aFileDto.setConfirm(true);
                fileService.update(aFileDto);
                return null;
            });
        }

    }
}
