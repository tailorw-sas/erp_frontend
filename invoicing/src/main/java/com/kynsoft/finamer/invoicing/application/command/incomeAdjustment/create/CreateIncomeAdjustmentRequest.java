package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create;

import lombok.Getter;
import lombok.Setter;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateIncomeAdjustmentRequest {
    private Status status;
    private UUID income;
    private String employee;
    private List<CreateIncomeAdjustment> adjustments;
}
