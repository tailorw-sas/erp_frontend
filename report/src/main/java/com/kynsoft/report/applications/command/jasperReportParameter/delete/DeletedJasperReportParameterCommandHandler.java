package com.kynsoft.report.applications.command.jasperReportParameter.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.JasperReportParameterDto;
import com.kynsoft.report.domain.services.IReportParameterService;
import org.springframework.stereotype.Component;

@Component
public class DeletedJasperReportParameterCommandHandler implements ICommandHandler<DeletedJasperReportParameterCommand> {

    private final IReportParameterService service;

    public DeletedJasperReportParameterCommandHandler(IReportParameterService service) {
        this.service = service;
    }

    @Override
    public void handle(DeletedJasperReportParameterCommand command) {
        JasperReportParameterDto dto = this.service.findById(command.getId());
        this.service.delete(dto);
    }
}
