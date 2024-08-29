package com.kynsoft.finamer.settings.application.command.managePaymentTransactionType.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManagePaymentTransactionTypeRequest {

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
