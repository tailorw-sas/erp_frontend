package com.kynsoft.report.applications.command.jasperReportParameter.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.JasperReportParameterDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import com.kynsoft.report.domain.services.IReportParameterService;
import com.kynsoft.report.infrastructure.enums.JasperParameterCategory;
import org.springframework.stereotype.Component;

@Component
public class CreateJasperReportParameterCommandHandler implements ICommandHandler<CreateJasperReportParameterCommand> {

    private final IReportParameterService service;
    private final IJasperReportTemplateService reportTemplateService;

    public CreateJasperReportParameterCommandHandler(IReportParameterService service, IJasperReportTemplateService reportTemplateService) {
        this.service = service;
        this.reportTemplateService = reportTemplateService;
    }

    @Override
    public void handle(CreateJasperReportParameterCommand command) {
        JasperReportTemplateDto jasperReportTemplateDto = reportTemplateService.findById(command.getReportId());
        this.service.create(new JasperReportParameterDto(command.getId(), command.getParamName(), command.getType(), command.getModule(),
                command.getService(), command.getLabel(), command.getComponentType(), jasperReportTemplateDto, command.getParameterPosition(),
                command.getDependentField(),command.getFilterKeyValue(),"", JasperParameterCategory.COMPLEMENTARY
        ));
    }
}
