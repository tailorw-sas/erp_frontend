package com.kynsoft.finamer.settings.application.command.manageReportParamType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageReportParamTypeDto;
import com.kynsoft.finamer.settings.domain.rules.manageReportParamType.ManageReportParamTypeNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.rules.manageReportParamType.ManageReportParamTypeNameMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageReportParamTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageReportParamTypeCommandHandler implements ICommandHandler<CreateManageReportParamTypeCommand> {

    private final IManageReportParamTypeService service;

    public CreateManageReportParamTypeCommandHandler(IManageReportParamTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageReportParamTypeCommand command) {
        RulesChecker.checkRule(new ManageReportParamTypeNameMustBeUniqueRule(service, command.getName(), command.getId()));
        RulesChecker.checkRule(new ManageReportParamTypeNameMustBeNullRule(command.getName()));

        service.create(new ManageReportParamTypeDto(
                command.getId(),
                command.getStatus(),
                command.getName(),
                command.getLabel(),
                command.getHotel(),
                command.getSource(),
                command.getDescription()
        ));
    }
}
