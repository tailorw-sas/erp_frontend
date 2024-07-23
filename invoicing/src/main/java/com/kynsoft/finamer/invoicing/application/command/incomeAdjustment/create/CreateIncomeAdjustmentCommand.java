package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateIncomeAdjustmentCommand implements ICommand {

    private UUID id;
    private Status status;
    private UUID income;
    private UUID transactionType;
    private Double amount;
    private LocalDate date;
    private String remark;
    private String employee;

    public CreateIncomeAdjustmentCommand(Status status, UUID income, UUID transactionType, Double amount, LocalDate date, String remark, String employee) {
        this.id = UUID.randomUUID();
        this.status = status;
        this.income = income;
        this.transactionType = transactionType;
        this.amount = amount;
        this.date = date;
        this.remark = remark;
        this.employee = employee;
    }

    public static CreateIncomeAdjustmentCommand fromRequest(CreateIncomeAdjustmentRequest request) {
        return new CreateIncomeAdjustmentCommand(
                request.getStatus(),
                request.getIncome(),
                request.getTransactionType(),
                request.getAmount(),
                request.getDate(),
                request.getRemark(),
                request.getEmployee()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateIncomeAdjustmentMessage(id);
    }
}
