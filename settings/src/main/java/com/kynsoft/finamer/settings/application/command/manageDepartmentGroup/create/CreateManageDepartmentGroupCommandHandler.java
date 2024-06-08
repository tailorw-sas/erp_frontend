package com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageDepartmentGroupDto;
import com.kynsoft.finamer.settings.domain.rules.manageDepartmentGroup.ManageDepartmentGroupCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageDepartmentGroup.ManageDepartmentGroupCodeSizeRule;
import com.kynsoft.finamer.settings.domain.services.IManageDepartmentGroupService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageDepartmentGroupCommandHandler implements ICommandHandler<CreateManageDepartmentGroupCommand> {

    private final IManageDepartmentGroupService service;

    public CreateManageDepartmentGroupCommandHandler(IManageDepartmentGroupService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageDepartmentGroupCommand command) {
        RulesChecker.checkRule(new ManageDepartmentGroupCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageDepartmentGroupCodeMustBeUniqueRule(service, command.getCode(), command.getId()));

        service.create(new ManageDepartmentGroupDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getIsActive()
        ));
    }
}
