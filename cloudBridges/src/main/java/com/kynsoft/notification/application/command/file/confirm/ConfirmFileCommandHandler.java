//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kynsoft.notification.application.command.file.confirm;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.response.FileDto;
import com.kynsoft.notification.domain.service.IAFileService;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ConfirmFileCommandHandler implements ICommandHandler<ConfirmFileCommand> {
    private final IAFileService fileService;

    public ConfirmFileCommandHandler(IAFileService fileService) {
        this.fileService = fileService;
    }

    public void handle(ConfirmFileCommand command) {
        List<FileDto> list = this.fileService.findByIds(command.getIds());
        if (!list.isEmpty()) {
            list.stream().map((aFileDto) -> {
                aFileDto.setConfirmed(true);
                this.fileService.update(aFileDto);
                return null;
            });
        }

    }
}
