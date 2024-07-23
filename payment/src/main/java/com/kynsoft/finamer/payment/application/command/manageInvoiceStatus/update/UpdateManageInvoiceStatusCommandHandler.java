package com.kynsoft.finamer.payment.application.command.manageInvoiceStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceStatusService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageInvoiceStatusCommandHandler implements ICommandHandler<UpdateManageInvoiceStatusCommand> {

    private final IManageInvoiceStatusService service;

    public UpdateManageInvoiceStatusCommandHandler(IManageInvoiceStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageInvoiceStatusCommand command) {
        ManageInvoiceStatusDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        this.service.update(dto);
    }

}
