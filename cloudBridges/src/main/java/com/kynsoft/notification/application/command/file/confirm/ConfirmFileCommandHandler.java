package com.kynsoft.notification.application.command.file.confirm;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.FileDto;
import com.kynsoft.notification.domain.service.IAFileService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfirmFileCommandHandler implements ICommandHandler<ConfirmFileCommand> {


    private final IAFileService fileService;


    public ConfirmFileCommandHandler( IAFileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public void handle(ConfirmFileCommand command) {
        List<FileDto> list = fileService.findByIds(command.getIds());
        if (!list.isEmpty()) {
            list.stream().map(aFileDto -> {
                aFileDto.setConfirm(true);
                fileService.update(aFileDto);
                return null;
            });
        }

    }
}
