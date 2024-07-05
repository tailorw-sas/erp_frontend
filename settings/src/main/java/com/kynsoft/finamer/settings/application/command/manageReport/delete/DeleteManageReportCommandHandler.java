package com.kynsoft.finamer.settings.application.command.manageReport.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageReportDto;
import com.kynsoft.finamer.settings.domain.services.IManageReportService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageReportCommandHandler implements ICommandHandler<DeleteManageReportCommand> {

    private final IManageReportService service;

    public DeleteManageReportCommandHandler(IManageReportService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageReportCommand command) {
        ManageReportDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
