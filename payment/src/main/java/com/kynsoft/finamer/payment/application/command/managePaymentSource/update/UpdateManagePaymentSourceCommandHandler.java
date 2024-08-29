package com.kynsoft.finamer.payment.application.command.managePaymentSource.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentSourceService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManagePaymentSourceCommandHandler implements ICommandHandler<UpdateManagePaymentSourceCommand> {

    private final IManagePaymentSourceService service;

    public UpdateManagePaymentSourceCommandHandler(IManagePaymentSourceService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManagePaymentSourceCommand command) {

        ManagePaymentSourceDto update = this.service.findById(command.getId());
        update.setName(command.getName());
        update.setStatus(command.getStatus());
        update.setExpense(command.getExpense());
        service.update(update);
    }
}
