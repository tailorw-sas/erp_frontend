package com.kynsoft.finamer.settings.application.command.managerTransactionStatus.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.rules.manageTransactionStatus.ManageTransactionStatusCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageTransactionStatus.ManageTransactionStatusCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageTransactionStatus.ManageTransactionStatusNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.rules.manageTransactionStatus.ManageTransactionStatusNavigateMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageTransactionStatusService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageTransactionStatusCommandHandler implements ICommandHandler<CreateManageTransactionStatusCommand> {

    private final IManageTransactionStatusService service;

    public CreateManageTransactionStatusCommandHandler(IManageTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageTransactionStatusCommand command) {
        RulesChecker.checkRule(new ManageTransactionStatusCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageTransactionStatusNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageTransactionStatusNavigateMustBeNullRule(command.getNavigate()));
        RulesChecker.checkRule(new ManageTransactionStatusCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageTransactionStatusDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getNavigate(),
                command.getEnablePayment(),
                command.getVisible(),
                command.getStatus()
        ));
    }
}
