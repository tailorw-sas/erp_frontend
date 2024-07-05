package com.kynsoft.finamer.invoicing.application.command.manageInvoiceType.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceTypeService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageInvoiceTypeCommandHandler implements ICommandHandler<UpdateManageInvoiceTypeCommand> {

    private final IManageInvoiceTypeService service;

    public UpdateManageInvoiceTypeCommandHandler(IManageInvoiceTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageInvoiceTypeCommand command) {


        ManageInvoiceTypeDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();


        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);

    }
}
