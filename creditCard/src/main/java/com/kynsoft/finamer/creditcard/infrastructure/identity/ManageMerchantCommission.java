package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantCommissionDto;
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
@Table(name = "manage_merchant_commission")
public class ManageMerchantCommission implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_merchant_id")
    private ManageMerchant managerMerchant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_credit_cart_type_id")
    private ManageCreditCardType manageCreditCartType;

    private Double commission;
    private String calculationType;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    public ManageMerchantCommission(ManageMerchantCommissionDto dto) {
        this.id = dto.getId();
        this.managerMerchant = new ManageMerchant(dto.getManagerMerchant());
        this.manageCreditCartType = new ManageCreditCardType(dto.getManageCreditCartType());
        this.commission = dto.getCommission();
        this.calculationType = dto.getCalculationType();
    }

    public ManageMerchantCommissionDto toAggregate() {
        return new ManageMerchantCommissionDto(
                id,
                managerMerchant != null ? managerMerchant.toAggregate() : null,
                manageCreditCartType != null ? manageCreditCartType.toAggregate() : null,
                commission, calculationType);
    }

}
