package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dto.IncomeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateIncomeAdjustmentCommand implements ICommand {

    private UUID id;
    private Status status;
    private UUID income;
    private UUID transactionType;
    private UUID paymentTransactionType;
    private Double amount;
    private LocalDate date;
    private String remark;

    private IncomeDto incomeResponse;

    public UpdateIncomeAdjustmentCommand(UUID id, Status status, UUID income, UUID transactionType,UUID paymentTransactionType, Double amount, LocalDate date, String remark) {
        this.id = id;
        this.status = status;
        this.income = income;
        this.transactionType = transactionType;
        this.amount = amount;
        this.date = date;
        this.remark = remark;
        this.paymentTransactionType = paymentTransactionType;
    }

    public static UpdateIncomeAdjustmentCommand fromRequest(UpdateIncomeAdjustmentRequest request, UUID id) {
        return new UpdateIncomeAdjustmentCommand(
                id,
                request.getStatus(),
                request.getIncome(),
                request.getTransactionType(),
                request.getPaymentTransactionType(),
                request.getAmount(),
                request.getDate(),
                request.getRemark()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateIncomeAdjustmentMessage(incomeResponse);
    }
}
