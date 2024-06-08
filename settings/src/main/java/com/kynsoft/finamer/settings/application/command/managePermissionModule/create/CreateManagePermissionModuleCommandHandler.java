package com.kynsoft.finamer.settings.application.command.managePermissionModule.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagePermissionModuleDto;
import com.kynsoft.finamer.settings.domain.rules.managePermissionModule.ManagePermissionModuleCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managePermissionModule.ManagePermissionModuleCodeSizeRule;
import com.kynsoft.finamer.settings.domain.services.IManagePermissionModuleService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagePermissionModuleCommandHandler implements ICommandHandler<CreateManagePermissionModuleCommand> {

    private final IManagePermissionModuleService service;

    public CreateManagePermissionModuleCommandHandler(IManagePermissionModuleService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagePermissionModuleCommand command) {
        RulesChecker.checkRule(new ManagePermissionModuleCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManagePermissionModuleCodeMustBeUniqueRule(service, command.getCode(), command.getId()));

        service.create(new ManagePermissionModuleDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getIsActive()
        ));
    }
}
