package com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceTransactionType.ManageInvoiceTransactionTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceTransactionType.ManageInvoiceTransactionTypeCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceTransactionType.ManageInvoiceTransactionTypeNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageInvoiceTransactionTypeCommandHandler implements ICommandHandler<CreateManageInvoiceTransactionTypeCommand> {

    private final IManageInvoiceTransactionTypeService service;

    public CreateManageInvoiceTransactionTypeCommandHandler(IManageInvoiceTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageInvoiceTransactionTypeCommand command) {
        RulesChecker.checkRule(new ManageInvoiceTransactionTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageInvoiceTransactionTypeNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageInvoiceTransactionTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageInvoiceTransactionTypeDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getIsAgencyRateAmount(),
                command.getIsNegative(),
                command.getIsPolicyCredit(),
                command.getIsRemarkRequired(),
                command.getMinNumberOfCharacters(),
                command.getDefaultRemark()
        ));
    }
}
