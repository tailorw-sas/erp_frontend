package com.kynsoft.finamer.settings.application.command.manageRegion.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageRegionDto;
import com.kynsoft.finamer.settings.domain.services.IManageRegionService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageRegionCommandHandler implements ICommandHandler<DeleteManageRegionCommand> {

    private final IManageRegionService service;

    public DeleteManageRegionCommandHandler(IManageRegionService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageRegionCommand command) {
        ManageRegionDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
