package com.kynsoft.report.applications.command.jasperReportParameter.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.JasperReportParameterDto;
import com.kynsoft.report.domain.services.IReportParameterService;
import com.kynsoft.report.infrastructure.enums.JasperParameterCategory;
import org.springframework.stereotype.Component;

@Component
public class UpdateJasperReportParameterCommandHandler implements ICommandHandler<UpdateJasperReportParameterCommand> {

    private final IReportParameterService service;

    public UpdateJasperReportParameterCommandHandler(IReportParameterService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateJasperReportParameterCommand command) {
        JasperReportParameterDto reportParameterDto = service.findById(command.getId());
        reportParameterDto.setService(command.getService());
        reportParameterDto.setLabel(command.getLabel());
        reportParameterDto.setModule(command.getModule());
        reportParameterDto.setType(command.getType());
        reportParameterDto.setComponentType(command.getComponentType());
        reportParameterDto.setParameterPosition(command.getParameterPosition());
        reportParameterDto.setDependentField(command.getDependentField());
        reportParameterDto.setFilterKeyValue(command.getFilterKeyValue());
        reportParameterDto.setDataValueStatic(command.getDataValueStatic());
        reportParameterDto.setParameterCategory(JasperParameterCategory.fromString(command.getParameterCategory()));
        this.service.update(reportParameterDto);

    }
}
