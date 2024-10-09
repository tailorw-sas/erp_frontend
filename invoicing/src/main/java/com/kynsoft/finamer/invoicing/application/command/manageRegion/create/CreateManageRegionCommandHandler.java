package com.kynsoft.finamer.invoicing.application.command.manageRegion.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRegionDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRegionService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageRegionCommandHandler implements ICommandHandler<CreateManageRegionCommand> {

    private final IManageRegionService service;

    public CreateManageRegionCommandHandler(IManageRegionService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageRegionCommand command) {
        service.create(new ManageRegionDto(
                command.getId(),
                command.getCode(),
                command.getName()
        ));
    }
}
