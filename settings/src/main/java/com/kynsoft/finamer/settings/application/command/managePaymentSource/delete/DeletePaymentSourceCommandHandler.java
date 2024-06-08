package com.kynsoft.finamer.settings.application.command.managePaymentSource.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentSourceService;
import org.springframework.stereotype.Component;

@Component
public class DeletePaymentSourceCommandHandler implements ICommandHandler<DeleteManagePaymentSourceCommand> {

    private final IManagePaymentSourceService service;

    public DeletePaymentSourceCommandHandler(IManagePaymentSourceService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagePaymentSourceCommand command) {
        ManagePaymentSourceDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
