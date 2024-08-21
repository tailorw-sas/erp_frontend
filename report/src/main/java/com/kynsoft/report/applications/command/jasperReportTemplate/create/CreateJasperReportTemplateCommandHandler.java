package com.kynsoft.report.applications.command.jasperReportTemplate.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.rules.jasperReport.ManageJasperReportCodeMustBeUniqueRule;
import com.kynsoft.report.domain.rules.jasperReport.ManageReportCodeMustBeNullRule;
import com.kynsoft.report.domain.rules.jasperReport.ManageReportNameMustBeNullRule;
import com.kynsoft.report.domain.rules.jasperReport.ManageReportParentIndexMustBeNullRule;
import com.kynsoft.report.domain.services.IDBConectionService;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import org.springframework.stereotype.Component;

@Component
public class CreateJasperReportTemplateCommandHandler implements ICommandHandler<CreateJasperReportTemplateCommand> {

    private final IJasperReportTemplateService service;

    private final IDBConectionService conectionService;

    public CreateJasperReportTemplateCommandHandler(IJasperReportTemplateService service, IDBConectionService conectionService) {
        this.service = service;
        this.conectionService = conectionService;
    }

    @Override
    public void handle(CreateJasperReportTemplateCommand command) {

        RulesChecker.checkRule(new ManageReportCodeMustBeNullRule(command.getCode()));
        RulesChecker.checkRule(new ManageReportNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageReportParentIndexMustBeNullRule(command.getParentIndex()));
        RulesChecker.checkRule(new ManageJasperReportCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        DBConectionDto conectionDto = command.getDbConection() != null ? this.conectionService.findById(command.getDbConection()) : null;
        this.service.create(new JasperReportTemplateDto(
                command.getId(), 
                command.getCode(), 
                command.getName(), 
                command.getDescription(), 
                command.getFile(),
                command.getType(),
                command.getParameters(),
                command.getParentIndex(),
                command.getMenuPosition(),
                command.getLanPath(),
                command.getWeb(),
                command.getSubMenu(),
                command.getSendEmail(),
                command.getInternal(),
                command.getHighRisk(),
                command.getVisible(),
                command.getCancel(),
                command.getRootIndex(),
                command.getLanguage(),
                command.getStatus(),
                conectionDto,
                command.getQuery()
        ));
    }
}
