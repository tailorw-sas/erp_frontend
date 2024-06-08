package com.kynsoft.report.applications.command.jasperReportTemplate.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import org.springframework.stereotype.Component;

@Component
public class DeleteJasperReportTemplateCommandHandler implements ICommandHandler<DeleteJasperReportTemplateCommand> {

    private final IJasperReportTemplateService serviceImpl;

    public DeleteJasperReportTemplateCommandHandler(IJasperReportTemplateService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteJasperReportTemplateCommand command) {

        JasperReportTemplateDto delete = this.serviceImpl.findById(command.getId());
        serviceImpl.delete(delete);
    }

}
