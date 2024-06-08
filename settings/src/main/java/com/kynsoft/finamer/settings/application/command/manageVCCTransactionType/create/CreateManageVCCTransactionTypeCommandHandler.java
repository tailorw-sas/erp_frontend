package com.kynsoft.finamer.settings.application.command.manageVCCTransactionType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageVCCTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.rules.manageVCCTransactionType.ManageVCCTransactionTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageVCCTransactionType.ManageVCCTransactionTypeCodeSizeRule;
import com.kynsoft.finamer.settings.domain.services.IManageVCCTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageVCCTransactionTypeCommandHandler implements ICommandHandler<CreateManageVCCTransactionTypeCommand> {

    private final IManageVCCTransactionTypeService service;

    public CreateManageVCCTransactionTypeCommandHandler(IManageVCCTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageVCCTransactionTypeCommand command) {
        RulesChecker.checkRule(new ManageVCCTransactionTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageVCCTransactionTypeCodeMustBeUniqueRule(service, command.getCode(), command.getId()));

        service.create(new ManageVCCTransactionTypeDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getIsActive()
        ));
    }
}
