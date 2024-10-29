package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantCommissionDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_merchant_commission")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_merchant_commission",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageMerchantCommission implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_merchant_id")
    private ManagerMerchant managerMerchant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_credit_cart_type_id")
    private ManageCreditCardType manageCreditCartType;

    private Double commission;
    private String calculationType;
    private String description;
    private LocalDate fromDate;

    @Column(nullable = true)
    private LocalDate toDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    public ManageMerchantCommission(ManageMerchantCommissionDto dto) {
        this.id = dto.getId();
        this.managerMerchant = new ManagerMerchant(dto.getManagerMerchant());
        this.manageCreditCartType = new ManageCreditCardType(dto.getManageCreditCartType());
        this.commission = dto.getCommission();
        this.calculationType = dto.getCalculationType();
        this.description = dto.getDescription();
        this.fromDate = dto.getFromDate();
        this.toDate = dto.getToDate();
        this.status = dto.getStatus();
    }

    public ManageMerchantCommissionDto toAggregate() {
        return new ManageMerchantCommissionDto(
                id,
                managerMerchant != null ? managerMerchant.toAggregate() : null,
                manageCreditCartType != null ? manageCreditCartType.toAggregate() : null,
                commission, calculationType, description, fromDate, toDate,status);
    }

}
