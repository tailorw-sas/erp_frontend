package com.kynsoft.finamer.settings.application.command.manageReport.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageReportDto;
import com.kynsoft.finamer.settings.domain.rules.manageReport.ManageReportCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageReport.ManageReportCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageReport.ManageReportNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageReportService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageReportCommandHandler implements ICommandHandler<CreateManageReportCommand> {

    private final IManageReportService service;

    public CreateManageReportCommandHandler(IManageReportService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageReportCommand command) {
        RulesChecker.checkRule(new ManageReportCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageReportNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageReportCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageReportDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getModuleId(),
                command.getModuleName()
        ));
    }
}
