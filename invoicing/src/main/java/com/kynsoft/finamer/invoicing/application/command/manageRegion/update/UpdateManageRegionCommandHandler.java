package com.kynsoft.finamer.invoicing.application.command.manageRegion.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRegionDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRegionService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageRegionCommandHandler implements ICommandHandler<UpdateManageRegionCommand> {

    private final IManageRegionService service;

    public UpdateManageRegionCommandHandler(IManageRegionService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageRegionCommand command) {
        ManageRegionDto dto = service.findById(command.getId());
        dto.setName(command.getName());
        this.service.update(dto);
    }
}
