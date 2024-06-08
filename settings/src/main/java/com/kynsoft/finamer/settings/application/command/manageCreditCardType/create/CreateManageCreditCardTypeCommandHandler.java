package com.kynsoft.finamer.settings.application.command.manageCreditCardType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.settings.domain.rules.manageCreditCardType.ManageCreditCardTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageCreditCardType.ManageCreditCardTypeFirstDigitMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageCreditCardType.ManagerCreditCardTypeCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageCreditCardType.ManagerCreditCardTypeNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageCreditCardTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageCreditCardTypeCommandHandler implements ICommandHandler<CreateManageCreditCardTypeCommand> {

    private final IManageCreditCardTypeService service;

    public CreateManageCreditCardTypeCommandHandler(IManageCreditCardTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageCreditCardTypeCommand command) {
        RulesChecker.checkRule(new ManagerCreditCardTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManagerCreditCardTypeNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageCreditCardTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));
        RulesChecker.checkRule(new ManageCreditCardTypeFirstDigitMustBeUniqueRule(this.service, command.getFirstDigit(), command.getId()));

        service.create(new ManageCreditCardTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getFirstDigit(),
                command.getStatus()
        ));
    }
}
