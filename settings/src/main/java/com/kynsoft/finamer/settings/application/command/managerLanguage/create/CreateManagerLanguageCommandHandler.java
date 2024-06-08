package com.kynsoft.finamer.settings.application.command.managerLanguage.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagerLanguageDto;
import com.kynsoft.finamer.settings.domain.rules.managerLanguage.ManagerLanguageCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managerLanguage.ManagerLanguageCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.managerLanguage.ManagerLanguageNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManagerLanguageService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerLanguageCommandHandler implements ICommandHandler<CreateManagerLanguageCommand> {

    private final IManagerLanguageService service;

    public CreateManagerLanguageCommandHandler(IManagerLanguageService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagerLanguageCommand command) {
        RulesChecker.checkRule(new ManagerLanguageCodeMustBeUniqueRule(service, command.getCode(), command.getId()));
        RulesChecker.checkRule(new ManagerLanguageCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManagerLanguageNameMustBeNullRule(command.getName()));

        service.create(new ManagerLanguageDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getIsEnabled()
        ));
    }
}
