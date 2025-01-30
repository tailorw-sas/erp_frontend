package com.kynsof.share.core.domain.http.entity.income.ajustment;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NewIncomeAdjustmentRequest {

    private UUID transactionType;
    private Double amount;
    private String date;
    private String remark;
}
