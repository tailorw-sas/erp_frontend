package com.kynsoft.finamer.payment.application.command.manageAgencyType.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyTypeService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageAgencyTypeCommandHandler implements ICommandHandler<UpdateManageAgencyTypeCommand> {

    private final IManageAgencyTypeService service;

    public UpdateManageAgencyTypeCommandHandler(IManageAgencyTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageAgencyTypeCommand command) {

        ManageAgencyTypeDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
        }
    }

}
