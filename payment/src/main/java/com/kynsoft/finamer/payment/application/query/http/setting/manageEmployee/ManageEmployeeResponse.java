package com.kynsoft.finamer.payment.application.query.http.setting.manageEmployee;

import java.io.Serializable;
import java.util.UUID;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageEmployeeResponse  implements IResponse, Serializable {
    private UUID id;
    private String code;
    private String status;
    private String description;

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
    private Boolean debit;
    private boolean expenseToBooking;

    public ManagePaymentTransactionTypeDto createObject() {
        return new ManagePaymentTransactionTypeDto(id, code, name, status, cash, deposit, applyDeposit, remarkRequired, minNumberOfCharacter, defaultRemark, defaults, paymentInvoice, debit);
    }
}
