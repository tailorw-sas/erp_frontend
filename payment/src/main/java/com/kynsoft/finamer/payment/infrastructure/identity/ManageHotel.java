package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
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
@Table(name = "manage_hotel")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_hotel",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageHotel implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    private String code;

    @Column(nullable = true)
    private Boolean deleted = false;

    private String name;
    private String status;

    @Column(nullable = true)
    private Boolean applyByTradingCompany;

    @Column(nullable = true)
    private UUID manageTradingCompany;

    @Column(nullable = true)
    private Boolean autoApplyCredit;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deletedAt;

    public ManageHotel(ManageHotelDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.applyByTradingCompany = dto.getApplyByTradingCompany();
        this.manageTradingCompany = dto.getManageTradingCompany();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.autoApplyCredit = dto.getAutoApplyCredit();
    }

    public ManageHotelDto toAggregate() {
        return new ManageHotelDto(
                id, code, name, status, applyByTradingCompany, manageTradingCompany, autoApplyCredit
        );
    }
}
