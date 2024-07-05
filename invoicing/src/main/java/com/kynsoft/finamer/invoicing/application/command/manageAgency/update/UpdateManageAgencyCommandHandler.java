package com.kynsoft.finamer.invoicing.application.command.manageAgency.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageAgencyCommandHandler implements ICommandHandler<UpdateManageAgencyCommand> {

    private final IManageAgencyService service;



    public UpdateManageAgencyCommandHandler(IManageAgencyService service) {
        this.service = service;

    }

    @Override
    public void handle(UpdateManageAgencyCommand command) {


        ManageAgencyDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);


        if (update.getUpdate() > 0) {
            this.service.update(dto);
        }

    }


}
