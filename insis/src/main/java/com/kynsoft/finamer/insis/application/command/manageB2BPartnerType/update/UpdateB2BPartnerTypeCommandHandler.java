package com.kynsoft.finamer.insis.application.command.manageB2BPartnerType.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.insis.domain.dto.ManageB2BPartnerTypeDto;
import com.kynsoft.finamer.insis.domain.services.IManageB2BPartnerTypeService;
import org.springframework.stereotype.Component;

@Component
public class UpdateB2BPartnerTypeCommandHandler implements ICommandHandler<UpdateB2BPartnerTypeCommand> {
    private final IManageB2BPartnerTypeService service;

    public UpdateB2BPartnerTypeCommandHandler(IManageB2BPartnerTypeService service){
        this.service = service;
    }

    @Override
    public void handle(UpdateB2BPartnerTypeCommand command) {
        ManageB2BPartnerTypeDto dto = service.findById(command.getId());
        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setCode, command.getCode(), dto.getCode(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        dto.setUpdatedAt(command.getUpdatedAt());

        service.update(dto);
    }
}
