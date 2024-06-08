package com.kynsoft.finamer.settings.application.command.manageRatePlan.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.settings.domain.services.IManageRatePlanService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageRatePlanCommandHandler implements ICommandHandler<DeleteManageRatePlanCommand> {

    private final IManageRatePlanService service;

    public DeleteManageRatePlanCommandHandler(IManageRatePlanService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageRatePlanCommand command) {
        ManageRatePlanDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
