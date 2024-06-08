package com.kynsoft.finamer.settings.application.command.managePaymentTransactionType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.rules.managePaymentTransactionType.ManagePaymentTransactionTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managePaymentTransactionType.ManagePaymentTransactionTypeCodeSizeRule;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagePaymentTransactionTypeCommandHandler implements ICommandHandler<CreateManagePaymentTransactionTypeCommand> {

    private final IManagePaymentTransactionTypeService service;

    public CreateManagePaymentTransactionTypeCommandHandler(IManagePaymentTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagePaymentTransactionTypeCommand command) {
        RulesChecker.checkRule(new ManagePaymentTransactionTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManagePaymentTransactionTypeCodeMustBeUniqueRule(service, command.getCode(), command.getId()));

        service.create(new ManagePaymentTransactionTypeDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getCash(),
                command.getAgencyRateAmount(),
                command.getNegative(),
                command.getPolicyCredit(),
                command.getRemarkRequired(),
                command.getMinNumberOfCharacter()
                ,command.getDefaultRemark()
        ));
    }
}
