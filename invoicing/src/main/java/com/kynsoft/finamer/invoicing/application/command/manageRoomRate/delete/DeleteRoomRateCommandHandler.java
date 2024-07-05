package com.kynsoft.finamer.invoicing.application.command.manageRoomRate.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import org.springframework.stereotype.Component;

@Component
public class DeleteRoomRateCommandHandler implements ICommandHandler<DeleteRoomRateCommand> {

    private final IManageRoomRateService service;

    public DeleteRoomRateCommandHandler(IManageRoomRateService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteRoomRateCommand command) {
        ManageRoomRateDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
