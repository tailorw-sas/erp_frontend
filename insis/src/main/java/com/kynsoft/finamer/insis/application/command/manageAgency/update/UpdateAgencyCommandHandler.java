package com.kynsoft.finamer.insis.application.command.manageAgency.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.insis.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.insis.domain.services.IManageAgencyService;
import org.springframework.stereotype.Component;

@Component
public class UpdateAgencyCommandHandler implements ICommandHandler<UpdateAgencyCommand> {

    private final IManageAgencyService service;

    public UpdateAgencyCommandHandler(IManageAgencyService service){
        this.service = service;
    }

    @Override
    public void handle(UpdateAgencyCommand command) {
        ManageAgencyDto dto = service.findById(command.getId());
        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setAgencyAlias, command.getAgencyAlias(), dto.getAgencyAlias(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        dto.setUpdatedAt(command.getUpdatedAt());

        service.update(dto);

    }
}
