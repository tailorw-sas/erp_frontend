package com.kynsoft.finamer.settings.application.command.manageActionLog.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageActionLogDto;
import com.kynsoft.finamer.settings.domain.services.IManageActionLogService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageActionLogCommandHandler implements ICommandHandler<DeleteManageActionLogCommand> {

    private final IManageActionLogService service;

    public DeleteManageActionLogCommandHandler(IManageActionLogService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageActionLogCommand command) {
        ManageActionLogDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
