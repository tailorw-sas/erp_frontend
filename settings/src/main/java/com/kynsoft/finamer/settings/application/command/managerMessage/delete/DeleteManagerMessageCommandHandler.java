package com.kynsoft.finamer.settings.application.command.managerMessage.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagerMessageDto;
import com.kynsoft.finamer.settings.domain.services.IManagerMessageService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerMessageCommandHandler implements ICommandHandler<DeleteManagerMessageCommand> {

    private final IManagerMessageService service;

    public DeleteManagerMessageCommandHandler(IManagerMessageService service) {
        this.service = service;

    }

    @Override
    public void handle(DeleteManagerMessageCommand command) {
        ManagerMessageDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
