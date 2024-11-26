package com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManagePaymentTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.rules.managePaymentTransactionStatus.*;
import com.kynsoft.finamer.creditcard.domain.services.IManagePaymentTransactionStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateManagePaymentTransactionStatusCommandHandler implements ICommandHandler<CreateManagePaymentTransactionStatusCommand> {

    private final IManagePaymentTransactionStatus service;

    public CreateManagePaymentTransactionStatusCommandHandler(IManagePaymentTransactionStatus service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagePaymentTransactionStatusCommand command) {
        RulesChecker.checkRule(new ManagePaymentTransactionStatusNameMustNotBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManagePaymentTransactionStatusCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManagePaymentTransactionStatusCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        if (command.isInProgress()){
            RulesChecker.checkRule(new ManagePaymentTransactionStatusInProgressMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isCompleted()){
            RulesChecker.checkRule(new ManagePaymentTransactionStatusCompletedMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isCancelled()){
            RulesChecker.checkRule(new ManagePaymentTransactionStatusCancelledMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isApplied()){
            RulesChecker.checkRule(new ManagePaymentTransactionStatusAppliedMustBeUniqueRule(this.service, command.getId()));
        }

        List<ManagePaymentTransactionStatusDto> transactionStatusDtoList = this.service.findByIds(command.getNavigate());

        ManagePaymentTransactionStatusDto dto = new ManagePaymentTransactionStatusDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus(),
                command.getDescription(),
                command.isRequireValidation(),
                transactionStatusDtoList,
                command.isInProgress(),
                command.isCompleted(),
                command.isCancelled(),
                command.isApplied()
        );

        this.service.create(dto);
    }
}
