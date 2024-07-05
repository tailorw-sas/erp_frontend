package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
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

    @Column(unique = true)
    private String code;
    private String name;
    private Boolean cash;
    private Boolean deposit;
    private Boolean applyDeposit;
    private Boolean remarkRequired;
    private Integer minNumberOfCharacter;

    public ManagePaymentTransactionType(ManagePaymentTransactionTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.cash = dto.getCash();
        this.deposit = dto.getDeposit();
        this.applyDeposit = dto.getApplyDeposit();
        this.remarkRequired = dto.getRemarkRequired();
        this.minNumberOfCharacter = dto.getMinNumberOfCharacter();
    }

    public ManagePaymentTransactionTypeDto toAggregate() {
        return new ManagePaymentTransactionTypeDto(id, code, name, cash, deposit, applyDeposit, remarkRequired, minNumberOfCharacter);
    }

}
