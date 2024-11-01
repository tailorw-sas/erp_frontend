package com.kynsoft.finamer.invoicing.application.command.manageRatePlan.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRatePlanService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageRatePlanCommandHandler implements ICommandHandler<UpdateManageRatePlanCommand> {

    private final IManageRatePlanService service;

    public UpdateManageRatePlanCommandHandler(IManageRatePlanService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageRatePlanCommand command) {


        ManageRatePlanDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();


        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setName, command.getName(), test.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);

        service.update(test);
    }

}
