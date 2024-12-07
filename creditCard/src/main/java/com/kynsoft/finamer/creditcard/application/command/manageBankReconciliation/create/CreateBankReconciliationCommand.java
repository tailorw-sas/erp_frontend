package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.create;

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
public class CreateBankReconciliationCommand implements ICommand {

    private UUID id;
    private UUID merchantBankAccount;
    private UUID hotel;
    private Double amount;
    private Double detailsAmount;
    private LocalDateTime paidDate;
    private String remark;
    private Set<Long> transactions;
    private List<CreateBankReconciliationAdjustmentRequest> adjustmentTransactions;
    private List<Long> adjustmentTransactionIds = new ArrayList<>();
    private Long reconciliationId;
    private String employee;

    public CreateBankReconciliationCommand(UUID merchantBankAccount, UUID hotel, Double amount, Double detailsAmount, LocalDateTime paidDate, String remark, Set<Long> transactions, List<CreateBankReconciliationAdjustmentRequest> adjustmentTransactions, String  employee) {
        this.id = UUID.randomUUID();
        this.merchantBankAccount = merchantBankAccount;
        this.hotel = hotel;
        this.amount = amount;
        this.detailsAmount = detailsAmount;
        this.paidDate = paidDate;
        this.remark = remark;
        this.transactions = transactions;
        this.adjustmentTransactions = adjustmentTransactions;
        this.employee = employee;
    }

    public static CreateBankReconciliationCommand fromRequest(CreateBankReconciliationRequest request){
        return new CreateBankReconciliationCommand(
                request.getMerchantBankAccount(),
                request.getHotel(),
                request.getAmount(),
                request.getDetailsAmount(),
                request.getPaidDate(),
                request.getRemark(),
                request.getTransactions(),
                request.getAdjustmentTransactions(),
                request.getEmployee()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateBankReconciliationMessage(id, reconciliationId, adjustmentTransactionIds);
    }
}
