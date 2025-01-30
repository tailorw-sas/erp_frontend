package com.kynsof.share.core.domain.http.entity.income.ajustment;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateIncomeAdjustmentRequest {

    private String status;
    private UUID income;
    private String employee;
    private List<NewIncomeAdjustmentRequest> adjustments;
}
