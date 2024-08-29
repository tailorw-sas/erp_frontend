package com.kynsoft.finamer.settings.application.query.objectResponse;

import java.util.UUID;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManagePaymentTransactionTypeResponse  implements IResponse {
    private UUID id;
    private String code;
    private Status status;
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

    public ManagePaymentTransactionTypeResponse(ManagePaymentTransactionTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.cash = dto.getCash();
        this.agencyRateAmount =  dto.getAgencyRateAmount();
        this.negative =  dto.getNegative();
        this.policyCredit =  dto.getPolicyCredit();
        this.remarkRequired =  dto.getRemarkRequired();
        this.minNumberOfCharacter =  dto.getMinNumberOfCharacter();
        this.defaultRemark =  dto.getDefaultRemark();
        this.applyDeposit = dto.getApplyDeposit();
        this.deposit = dto.getDeposit();
        this.defaults = dto.getDefaults();
        this.antiToIncome = dto.getAntiToIncome();
        this.incomeDefault = dto.getIncomeDefault();
        this.paymentInvoice = dto.getPaymentInvoice();
    }

}
