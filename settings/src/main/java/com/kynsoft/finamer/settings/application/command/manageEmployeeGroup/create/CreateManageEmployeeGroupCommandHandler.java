package com.kynsoft.finamer.settings.application.command.manageEmployeeGroup.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeGroupDto;
import com.kynsoft.finamer.settings.domain.rules.manageEmployeeGroup.ManageEmployeeGroupCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageEmployeeGroup.ManageEmployeeGroupCodeSizeRule;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeGroupService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageEmployeeGroupCommandHandler implements ICommandHandler<CreateManageEmployeeGroupCommand> {

    private final IManageEmployeeGroupService service;

    public CreateManageEmployeeGroupCommandHandler(IManageEmployeeGroupService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageEmployeeGroupCommand command) {
        RulesChecker.checkRule(new ManageEmployeeGroupCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageEmployeeGroupCodeMustBeUniqueRule(service, command.getCode(), command.getId()));

        service.create(new ManageEmployeeGroupDto(
                command.getId(),
                command.getCode(),
                command.getStatus(),
                command.getName(),
                command.getDescription()
        ));
    }
}
