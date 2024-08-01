package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.AttachmentStatusHistoryDto;
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
@Table(name = "attachment_status_history")
public class AttachmentStatusHistory implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    private String description;

    private Long attachmentId;

    @ManyToOne(fetch = FetchType.EAGER)
    private ManageInvoice invoice;

    private String employee;

    private UUID employeeId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public AttachmentStatusHistory(AttachmentStatusHistoryDto dto) {
        this.id = dto.getId();
        this.description = dto.getDescription();
        this.attachmentId = dto.getAttachmentId();
        this.invoice = dto.getInvoice() != null ? new ManageInvoice(dto.getInvoice()) : null;
        this.employee = dto.getEmployee();
        this.employeeId = dto.getEmployeeId();
    }

    public AttachmentStatusHistoryDto toAggregate(){
        return  new AttachmentStatusHistoryDto(
                id,description, attachmentId,
                invoice != null ? invoice.toAggregate() : null,
                employee, employeeId, createdAt, updatedAt
        );
    }
}
