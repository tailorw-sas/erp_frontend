package com.kynsoft.finamer.creditcard.application.command.managerTransactionStatus.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.rules.manageTransactionStatus.*;
import com.kynsoft.finamer.creditcard.domain.services.IManageTransactionStatusService;
import org.springframework.stereotype.Component;

import java.util.List;

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
        //RulesChecker.checkRule(new ManageTransactionStatusNavigateMustBeNullRule(command.getNavigate()));
        RulesChecker.checkRule(new ManageTransactionStatusCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        if (command.isSentStatus()){
            RulesChecker.checkRule(new ManageTransactionSentStatusMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isRefundStatus()){
            RulesChecker.checkRule(new ManageTransactionRefundStatusMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isReceivedStatus()){
            RulesChecker.checkRule(new ManageTransactionReceivedStatusMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isCancelledStatus()){
            RulesChecker.checkRule(new ManageTransactionCancelledStatusMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isDeclinedStatus()){
            RulesChecker.checkRule(new ManageTransactionDeclinedStatusMustBeUniqueRule(this.service, command.getId()));
        }

        List<ManageTransactionStatusDto> navigate = this.service.findByIds(command.getNavigate().stream().toList());

        service.create(new ManageTransactionStatusDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                navigate,
                command.getEnablePayment(),
                command.getVisible(),
                command.getStatus(),
                command.isSentStatus(),
                command.isRefundStatus(),
                command.isReceivedStatus(),
                command.isCancelledStatus(),
                command.isDeclinedStatus()
        ));
    }
}
