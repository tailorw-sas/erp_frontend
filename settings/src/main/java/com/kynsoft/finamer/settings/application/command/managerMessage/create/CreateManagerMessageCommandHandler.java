package com.kynsoft.finamer.settings.application.command.managerMessage.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.settings.domain.dto.ManagerLanguageDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMessageDto;
import com.kynsoft.finamer.settings.domain.rules.managerMessage.ManagerMessageCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managerMessage.ManagerMessageCodeSizeRule;
import com.kynsoft.finamer.settings.domain.services.IManagerLanguageService;
import com.kynsoft.finamer.settings.domain.services.IManagerMessageService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerMessageCommandHandler implements ICommandHandler<CreateManagerMessageCommand> {

    private final IManagerMessageService service;

    private final IManagerLanguageService languageService;

    public CreateManagerMessageCommandHandler(IManagerMessageService service, IManagerLanguageService languageService) {
        this.service = service;
        this.languageService = languageService;
    }

    @Override
    public void handle(CreateManagerMessageCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getLanguage(), "id", "Language ID cannot be null."));
        ManagerLanguageDto languageDto = languageService.findById(command.getLanguage());

        RulesChecker.checkRule(new ManagerMessageCodeMustBeUniqueRule(service, command.getCode(), command.getId()));
        RulesChecker.checkRule(new ManagerMessageCodeSizeRule(command.getCode()));

        service.create(new ManagerMessageDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getType(),
                languageDto
        ));
    }
}
