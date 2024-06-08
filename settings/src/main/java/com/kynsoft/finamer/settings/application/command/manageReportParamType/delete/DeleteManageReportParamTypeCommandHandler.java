package com.kynsoft.finamer.settings.application.command.manageReportParamType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageReportParamTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageReportParamTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageReportParamTypeCommandHandler implements ICommandHandler<DeleteManageReportParamTypeCommand> {

    private final IManageReportParamTypeService service;

    public DeleteManageReportParamTypeCommandHandler(IManageReportParamTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageReportParamTypeCommand command) {
        ManageReportParamTypeDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
