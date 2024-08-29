package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_payment_transaction_type")
public class ManagePaymentTransactionType implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    private String code;
    private String name;
    @Enumerated(EnumType.STRING)
    private Status status;
    private Boolean cash;
    private Boolean deposit;
    private Boolean applyDeposit;
    private Boolean remarkRequired;
    private Integer minNumberOfCharacter;
    private String defaultRemark;
    @Column(columnDefinition = "boolean default false")
    private boolean defaults=false;
    private Boolean paymentInvoice;

    public ManagePaymentTransactionType(ManagePaymentTransactionTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = Status.valueOf(dto.getStatus());
        this.cash = dto.getCash();
        this.deposit = dto.getDeposit();
        this.applyDeposit = dto.getApplyDeposit();
        this.remarkRequired = dto.getRemarkRequired();
        this.minNumberOfCharacter = dto.getMinNumberOfCharacter();
        this.defaultRemark = dto.getDefaultRemark();
        this.defaults = dto.isDefaults();
        this.paymentInvoice = dto.getPaymentInvoice();
    }

    public ManagePaymentTransactionTypeDto toAggregate() {
        return new ManagePaymentTransactionTypeDto(id, code, name, status.name(), cash, deposit, applyDeposit, remarkRequired, minNumberOfCharacter, defaultRemark, defaults, paymentInvoice);
    }

}
