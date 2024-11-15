package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter

public class UpdateBankReconciliationCommand implements ICommand {

    private UUID id;
    private LocalDateTime paidDate;
    private String remark;
    private Set<Long> transactions;
    private List<UpdateBankReconciliationAdjustmentRequest> adjustmentTransactions;
    private List<Long> adjustmentTransactionIds = new ArrayList<>();
    private UUID reconcileStatus;
    private Double amount;
    private String employee;

    public UpdateBankReconciliationCommand(
            UUID id,
            LocalDateTime paidDate,
            String remark,
            Set<Long> transactions,
            List<UpdateBankReconciliationAdjustmentRequest> adjustmentTransactions,
            UUID reconcileStatus,
            Double amount,
            String employee) {
        this.id = id;
        this.paidDate = paidDate;
        this.remark = remark;
        this.transactions = transactions;
        this.adjustmentTransactions = adjustmentTransactions;
        this.reconcileStatus = reconcileStatus;
        this.amount = amount;
        this.employee = employee;
    }

    public static UpdateBankReconciliationCommand fromRequest(UUID id, UpdateBankReconciliationRequest request){
        return new UpdateBankReconciliationCommand(
                id,
                request.getPaidDate(),
                request.getRemark(),
                request.getTransactions(),
                request.getAdjustmentTransactions(),
                request.getReconcileStatus(),
                request.getAmount(),
                request.getEmployee()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateBankReconciliationMessage(id, adjustmentTransactionIds);
    }
}
