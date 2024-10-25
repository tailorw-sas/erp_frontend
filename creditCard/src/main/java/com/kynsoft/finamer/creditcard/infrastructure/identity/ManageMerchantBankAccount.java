package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantBankAccountDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_merchant_bank_account")
public class ManageMerchantBankAccount implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "manage_merchant_bank_account_merchants",
            joinColumns = @JoinColumn(name = "manage_merchant_bank_account_id"),
            inverseJoinColumns = @JoinColumn(name = "manage_merchant_id")
    )
    private Set<ManageMerchant> managerMerchant = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_bank_id")
    private ManagerBank manageBank;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
        name = "manage_merchant_bank_account_credit_card_types",
        joinColumns = @JoinColumn(name = "manage_merchant_bank_account_id"),
        inverseJoinColumns = @JoinColumn(name = "manage_credit_card_type_id")
    )
    private Set<ManageCreditCardType> creditCardTypes = new HashSet<>();

    private String accountNumber;
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    public ManageMerchantBankAccount(ManageMerchantBankAccountDto dto) {
        this.id = dto.getId();
        this.managerMerchant = dto.getManagerMerchant() != null ? dto.getManagerMerchant().stream().map(ManageMerchant::new).collect(Collectors.toSet()) : new HashSet<>();
        this.manageBank = dto.getManageBank() != null ? new ManagerBank(dto.getManageBank()) : null;
        this.accountNumber = dto.getAccountNumber();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.creditCardTypes = dto.getCreditCardTypes() != null ? dto.getCreditCardTypes().stream().map(ManageCreditCardType::new).collect(Collectors.toSet()) : new HashSet<>();
    }

    public ManageMerchantBankAccountDto toAggregate() {
        Set<ManageCreditCardTypeDto> ccardTypes = new HashSet<>();
        for (ManageCreditCardType creditCardType : this.creditCardTypes) {
            ccardTypes.add(creditCardType.toAggregate());
        }
        return new ManageMerchantBankAccountDto(
                id,
                managerMerchant != null ? managerMerchant.stream().map(ManageMerchant::toAggregate).collect(Collectors.toSet()) : new HashSet<>(),
                manageBank != null ? manageBank.toAggregate() : null,
                accountNumber, description, status, ccardTypes);
    }

    public ManageMerchantBankAccountDto toAggregateSimple(){
        return new ManageMerchantBankAccountDto(
                id,
                null,
                manageBank != null ? manageBank.toAggregate() : null,
                accountNumber,
                description,
                status,
                null
        );
    }

}
