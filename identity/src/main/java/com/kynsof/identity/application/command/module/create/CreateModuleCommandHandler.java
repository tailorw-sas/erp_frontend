package com.kynsof.identity.application.command.module.create;

import com.kynsof.identity.domain.dto.ModuleDto;
import com.kynsof.identity.domain.interfaces.service.IModuleService;
import com.kynsof.identity.domain.rules.module.ModuleDescriptionMustBeNullRule;
import com.kynsof.identity.domain.rules.module.ModuleNameMustBeNullRule;
import com.kynsof.identity.domain.rules.module.ModuleNameMustBeUniqueRule;
import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class CreateModuleCommandHandler implements ICommandHandler<CreateModuleCommand> {

    private final IModuleService service;

    public CreateModuleCommandHandler(IModuleService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateModuleCommand command) {
        RulesChecker.checkRule(new ModuleNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ModuleDescriptionMustBeNullRule(command.getDescription()));
        RulesChecker.checkRule(new ModuleNameMustBeUniqueRule(this.service, command.getName(), command.getId()));

        service.create(new ModuleDto(
                command.getId(),
                command.getName(),
                null,
                command.getDescription())
        );
    }
}
