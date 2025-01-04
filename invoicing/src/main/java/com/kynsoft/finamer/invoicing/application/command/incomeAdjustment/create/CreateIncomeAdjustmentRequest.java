package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateIncomeAdjustmentRequest {

    private Status status;
    private UUID income;
    private String employee;
    private List<NewIncomeAdjustmentRequest> adjustments;
}
