package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_payment_status")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_payment_status",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManagePaymentStatus {
    @Id
    @Column(name = "id")
    private UUID id;
    private String code;
    private String name;
    private String status;
    private Boolean applied;
    
    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean confirmed;
    
    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean cancelled;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean transit;

    public ManagePaymentStatus(ManagePaymentStatusDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.applied = dto.getApplied();
        this.confirmed = dto.isConfirmed();
        this.cancelled = dto.isCancelled();
        this.transit = dto.isTransit();
    }
    
    public ManagePaymentStatusDto toAggregate(){
        return new ManagePaymentStatusDto(id, code, name, status, applied, confirmed, cancelled, transit);
    }
}
