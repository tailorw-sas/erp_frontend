package com.kynsoft.report.applications.command.jasperReportTemplate.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.services.IAmazonClient;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import org.springframework.stereotype.Component;

@Component
public class CreateJasperReportTemplateCommandHandler implements ICommandHandler<CreateJasperReportTemplateCommand> {

    private final IJasperReportTemplateService service;
    private final IAmazonClient amazonClient;

    public CreateJasperReportTemplateCommandHandler(IJasperReportTemplateService service, IAmazonClient amazonClient) {
        this.service = service;
        this.amazonClient = amazonClient;
    }

    @Override
    public void handle(CreateJasperReportTemplateCommand command) {

        this.service.create(new JasperReportTemplateDto(
                command.getId(), 
                command.getCode(), 
                command.getName(), 
                command.getDescription(), 
                command.getFile(),
                command.getType(),
                command.getParameters()
        ));
    }
}
