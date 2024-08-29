package com.kynsoft.finamer.settings.domain.dto;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManagePaymentTransactionTypeDto {
    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean cash;
    private Boolean agencyRateAmount;
    private Boolean negative;
    private Boolean policyCredit;
    private Boolean remarkRequired;
    private Integer minNumberOfCharacter;
    private String defaultRemark;
    private Boolean deposit;
    private Boolean applyDeposit;
    private Boolean defaults;
    private Boolean antiToIncome;
    private Boolean incomeDefault;
    private Boolean paymentInvoice;

}
