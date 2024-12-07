package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceStatusDto;
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
@Table(name = "manage_invoice_status")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_invoice_status",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageInvoiceStatus implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    private String code;
    private String name;


    public ManageInvoiceStatus(ManageInvoiceStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();

        this.name = dto.getName();

    }


    public ManageInvoiceStatusDto toAggregate() {
        return new ManageInvoiceStatusDto(
                id, code, name
        );
    }
}
