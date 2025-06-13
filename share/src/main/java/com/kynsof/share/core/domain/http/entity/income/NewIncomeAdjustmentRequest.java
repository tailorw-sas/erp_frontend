package com.kynsof.share.core.domain.http.entity.income;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NewIncomeAdjustmentRequest {

    private UUID transactionType;
    private Double amount;
    private LocalDate date;
    private String remark;
}
