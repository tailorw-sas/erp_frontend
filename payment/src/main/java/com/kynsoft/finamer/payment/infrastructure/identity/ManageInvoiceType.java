package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceTypeDto;
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
@Table(name = "manage_invoice_type")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_invoice_type",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageInvoiceType implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    private String code;

    @Column(nullable = true)
    private Boolean deleted = false;

    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deletedAt;

    public ManageInvoiceType(ManageInvoiceTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
    }

    public ManageInvoiceTypeDto toAggregate() {
        return new ManageInvoiceTypeDto(
            id, code, name
        );
    }

}
