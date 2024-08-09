package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateIncomeAdjustmentCommand implements ICommand {

    private Status status;
    private UUID income;
    private String employee;
    private List<NewIncomeAdjustmentRequest> adjustments;

    public CreateIncomeAdjustmentCommand(Status status, UUID income, String employee, List<NewIncomeAdjustmentRequest> adjustments) {
        this.status = status;
        this.income = income;
        this.employee = employee;
        this.adjustments = adjustments;
    }

    public static CreateIncomeAdjustmentCommand fromRequest(CreateIncomeAdjustmentRequest request) {
        return new CreateIncomeAdjustmentCommand(
                request.getStatus(),
                request.getIncome(),
                request.getEmployee(),
                request.getAdjustments()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateIncomeAdjustmentMessage();
    }
}
