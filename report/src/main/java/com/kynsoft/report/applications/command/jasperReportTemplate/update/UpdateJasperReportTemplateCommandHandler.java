package com.kynsoft.report.applications.command.jasperReportTemplate.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.services.IAmazonClient;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import org.springframework.stereotype.Component;

@Component
public class UpdateJasperReportTemplateCommandHandler implements ICommandHandler<UpdateJasperReportTemplateCommand> {

    private final IJasperReportTemplateService service;
    private final IAmazonClient amazonClient;

    public UpdateJasperReportTemplateCommandHandler(IJasperReportTemplateService service, IAmazonClient amazonClient) {
        this.service = service;
        this.amazonClient = amazonClient;
    }

    @Override
    public void handle(UpdateJasperReportTemplateCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "JasperReportTemplate ID cannot be null."));
        JasperReportTemplateDto update = this.service.findById(command.getId());
        update.setTemplateContentUrl(command.getFile());
        UpdateIfNotNull.updateIfNotNull(update::setTemplateCode, command.getCode());
        UpdateIfNotNull.updateIfNotNull(update::setTemplateDescription, command.getDescription());
        UpdateIfNotNull.updateIfNotNull(update::setTemplateName, command.getName());
        UpdateIfNotNull.updateIfNotNull(update::setParameters, command.getParameters());
        update.setType(command.getType());

        this.service.update(update);
    }
}
