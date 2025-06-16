package com.kynsof.share.core.domain.http.entity.income.adjustment;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateAntiToIncomeAdjustmentRequest {

    private UUID transactionType;
    private Double amount;
    private String date;
    private String remark;
}
