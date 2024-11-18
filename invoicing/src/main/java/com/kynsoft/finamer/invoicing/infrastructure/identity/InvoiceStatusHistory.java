package com.kynsoft.finamer.invoicing.infrastructure.identity;


import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceStatusHistoryDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
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
@Table(name = "invoice_status_history")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "invoice_status_history",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class InvoiceStatusHistory  implements Serializable {
    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Invoice invoice;


    private String description;

    private String employee;

    @Enumerated(EnumType.STRING)
    private EInvoiceStatus invoiceStatus;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public InvoiceStatusHistory(InvoiceStatusHistoryDto dto){
        this.id = dto.getId();
        this.invoice = new Invoice(dto.getInvoice());
        this.description = dto.getDescription();
        this.employee =dto.getEmployee();
        this.invoiceStatus = dto.getInvoiceStatus() != null ? dto.getInvoiceStatus() : EInvoiceStatus.PROCECSED;
    }

    public InvoiceStatusHistoryDto toAggregate(){
        return new InvoiceStatusHistoryDto(id, invoice.toAggregate(), description, createdAt, employee, invoiceStatus);
    }

}
