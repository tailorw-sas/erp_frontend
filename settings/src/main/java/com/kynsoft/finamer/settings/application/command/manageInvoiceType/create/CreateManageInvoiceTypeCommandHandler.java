package com.kynsoft.finamer.settings.application.command.manageInvoiceType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceType.ManageInvoiceTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceType.ManageInvoiceTypeCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageInvoiceType.ManageInvoiceTypeNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageInvoiceTypeCommandHandler implements ICommandHandler<CreateManageInvoiceTypeCommand> {

    private final IManageInvoiceTypeService service;

    public CreateManageInvoiceTypeCommandHandler(IManageInvoiceTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageInvoiceTypeCommand command) {
        RulesChecker.checkRule(new ManageInvoiceTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageInvoiceTypeNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageInvoiceTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageInvoiceTypeDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getEnabledToPolicy()
        ));
    }
}
