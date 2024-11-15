package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceTransactionTypeDto;
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
@Table(name = "manage_invoice_transaction_type")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_invoice_transaction_type",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageInvoiceTransactionType implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    private String code;
    private String name;

    public ManageInvoiceTransactionType(ManageInvoiceTransactionTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();

        this.name = dto.getName();

    }

    public ManageInvoiceTransactionTypeDto toAggregate() {
        return new ManageInvoiceTransactionTypeDto(
                id, code, name
        );
    }
}
