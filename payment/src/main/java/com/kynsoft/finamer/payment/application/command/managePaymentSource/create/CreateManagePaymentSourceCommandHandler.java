package com.kynsoft.finamer.payment.application.command.managePaymentSource.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentSourceService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagePaymentSourceCommandHandler implements ICommandHandler<CreateManagePaymentSourceCommand> {

    private final IManagePaymentSourceService service;

    public CreateManagePaymentSourceCommandHandler(IManagePaymentSourceService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagePaymentSourceCommand command) {

        service.create( new ManagePaymentSourceDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus(),
                command.getExpense()
        ));
    }
}
