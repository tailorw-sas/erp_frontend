package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantBankAccountDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

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
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_merchant_id")
    private ManagerMerchant managerMerchant;

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

    @Column(nullable = true)
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deleteAt;

    public ManageMerchantBankAccount(ManageMerchantBankAccountDto dto) {
        this.id = dto.getId();
        this.managerMerchant = dto.getManagerMerchant() != null ? new ManagerMerchant(dto.getManagerMerchant()) : null;
        this.manageBank = dto.getManageBank() != null ? new ManagerBank(dto.getManageBank()) : null;
        this.accountNumber = dto.getAccountNumber();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        for (ManageCreditCardTypeDto creditCardType : dto.getCreditCardTypes()) {
            this.creditCardTypes.add(new ManageCreditCardType(creditCardType));
        }
    }

    public ManageMerchantBankAccountDto toAggregate() {
        Set<ManageCreditCardTypeDto> ccardTypes = new HashSet<>();
        for (ManageCreditCardType creditCardType : this.creditCardTypes) {
            ccardTypes.add(creditCardType.toAggregate());
        }
        return new ManageMerchantBankAccountDto(id, managerMerchant.toAggregate(), manageBank.toAggregate(), accountNumber, description, status, ccardTypes);
    }

}
