package com.kynsoft.finamer.creditcard.application.command.manageReconcileTransactionStatus.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageReconcileTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.rules.manageReconcileTransactionStatus.*;
import com.kynsoft.finamer.creditcard.domain.services.IManageReconcileTransactionStatusService;
import org.springframework.stereotype.Component;

import java.util.List;

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

        if (command.isCreated()){
            RulesChecker.checkRule(new ManageReconcileTransactionStatusCreatedMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isCancelled()){
            RulesChecker.checkRule(new ManageReconcileTransactionStatusCancelledMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isCompleted()){
            RulesChecker.checkRule(new ManageReconcileTransactionStatusCompletedMustBeUniqueRule(this.service, command.getId()));
        }

        List<ManageReconcileTransactionStatusDto> manageReconcileTransactionStatusDtoList = service.findByIds(command.getNavigate());
        service.create(new ManageReconcileTransactionStatusDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus(),
                command.getName(),
                command.getRequireValidation(),
                manageReconcileTransactionStatusDtoList,
                command.isCreated(),
                command.isCancelled(),
                command.isCompleted()
        ));
    }
}
