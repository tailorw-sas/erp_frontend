package com.kynsoft.finamer.invoicing.application.command.manageAgency.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.domain.services.IManagerClientService;

import org.springframework.stereotype.Component;

@Component
public class UpdateManageAgencyCommandHandler implements ICommandHandler<UpdateManageAgencyCommand> {

    private final IManageAgencyService service;
    private final IManagerClientService clientService;

    public UpdateManageAgencyCommandHandler(IManageAgencyService service, IManagerClientService clientService) {
        this.service = service;
        this.clientService = clientService;

    }

    @Override
    public void handle(UpdateManageAgencyCommand command) {

        ManageAgencyDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(),
                update::setUpdate);

        UpdateIfNotNull.updateEntity(dto::setClient, command.getClient(), dto.getClient().getId(), update::setUpdate,
                clientService::findById);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
        }

    }

}
