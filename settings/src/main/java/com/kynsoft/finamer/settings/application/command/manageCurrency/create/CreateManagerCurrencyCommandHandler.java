package com.kynsoft.finamer.settings.application.command.manageCurrency.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagerCurrencyDto;
import com.kynsoft.finamer.settings.domain.rules.managerCurrency.ManagerCurrencyCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managerCurrency.ManagerCurrencyCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.managerCurrency.ManagerCurrencyNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManagerCurrencyService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerCurrencyCommandHandler implements ICommandHandler<CreateManagerCurrencyCommand> {

    private final IManagerCurrencyService service;

    public CreateManagerCurrencyCommandHandler(IManagerCurrencyService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagerCurrencyCommand command) {
        RulesChecker.checkRule(new ManagerCurrencyCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManagerCurrencyNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManagerCurrencyCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManagerCurrencyDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getStatus()
        ));
    }
}
