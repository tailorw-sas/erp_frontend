package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantCurrencyDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_merchant_currency")
public class ManagerMerchantCurrency implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_merchant_id")
    private ManageMerchant managerMerchant;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_currency_id")
    private ManagerCurrency managerCurrency;

    private String value;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    public ManagerMerchantCurrency(ManagerMerchantCurrencyDto dto) {
        this.id = dto.getId();
        this.managerMerchant = dto.getManagerMerchant() != null ? new ManageMerchant(dto.getManagerMerchant()) : null;
        this.managerCurrency = dto.getManagerCurrency() != null ? new ManagerCurrency(dto.getManagerCurrency()) : null;
        this.value = dto.getValue();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
    }

    public ManagerMerchantCurrencyDto toAggregate() {
        return new ManagerMerchantCurrencyDto(id,
                managerMerchant != null ? managerMerchant.toAggregate() : null,
                managerCurrency != null ? managerCurrency.toAggregate() : null,
                value, description, status);
    }

}
