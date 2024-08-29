package com.kynsoft.finamer.invoicing.application.command.manageInvoiceStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageInvoiceStatusCommandHandler implements ICommandHandler<CreateManageInvoiceStatusCommand> {

    private final IManageInvoiceStatusService service;

    public CreateManageInvoiceStatusCommandHandler(IManageInvoiceStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageInvoiceStatusCommand command) {


        service.create(new ManageInvoiceStatusDto(
                command.getId(),
                command.getCode(),

                command.getName(),
                command.getShowClone()

        ));
    }
}
