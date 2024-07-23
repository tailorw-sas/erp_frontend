package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_invoice_attachment")
public class ManageAttachment {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(columnDefinition = "serial", name = "attachment_gen_id")
    @Generated(event = EventType.INSERT)
    private Long attachment_id;

    private String filename;

    private String employee;
    private UUID employeeId;

    private String file;

    private String remark;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_attachment_type")
    private ManageAttachmentType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_invoice")
    private ManageInvoice invoice;

    @Column(nullable = true)
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deletedAt;

    public ManageAttachment(ManageAttachmentDto dto) {

        this.id = dto.getId();
        this.attachment_id = dto.getAttachment_id();
        this.invoice = dto.getInvoice() != null ? new ManageInvoice(dto.getInvoice()) : null;
        this.filename = dto.getFilename();
        this.file = dto.getFile();
        this.remark = dto.getRemark();
        this.type = dto.getType() != null ? new ManageAttachmentType(dto.getType()) : null;
        this.employee = dto.getEmployee();
        this.employeeId = dto.getEmployeeId();
    }

    public ManageAttachmentDto toAggregate() {
        return new ManageAttachmentDto(id, attachment_id, filename, file, remark,
                type != null ? type.toAggregate() : null,
                invoice != null ? invoice.toAggregateSample() : null, employee, employeeId, createdAt);
    }

    public ManageAttachmentDto toAggregateSample() {
        return new ManageAttachmentDto(id, attachment_id, filename, file, remark,
                type != null ? type.toAggregate() : null,
                null, employee, employeeId,createdAt);
    }
}
