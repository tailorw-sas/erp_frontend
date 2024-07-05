package com.kynsoft.finamer.invoicing.application.command.manageRatePlan.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRatePlanService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageRatePlanCommandHandler implements ICommandHandler<CreateManageRatePlanCommand> {

    private final IManageRatePlanService service;

    public CreateManageRatePlanCommandHandler(IManageRatePlanService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageRatePlanCommand command) {

        service.create(new ManageRatePlanDto(
                command.getId(),
                command.getCode(),
                command.getName()

        ));
    }
}
