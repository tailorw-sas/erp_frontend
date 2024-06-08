package com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageReconcileTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.rules.manageReconcileTransactionStatus.ManageReconcileTransactionStatusCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageReconcileTransactionStatus.ManageReconcileTransactionStatusCodeSizeRule;
import com.kynsoft.finamer.settings.domain.services.IManageReconcileTransactionStatusService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageReconcileTransactionStatusCommandHandler implements ICommandHandler<CreateManageReconcileTransactionStatusCommand> {

    private final IManageReconcileTransactionStatusService service;

    public CreateManageReconcileTransactionStatusCommandHandler(IManageReconcileTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageReconcileTransactionStatusCommand command) {
        RulesChecker.checkRule(new ManageReconcileTransactionStatusCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageReconcileTransactionStatusCodeMustBeUniqueRule(service, command.getCode(), command.getId()));

        service.create(new ManageReconcileTransactionStatusDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName()
        ));
    }
}
