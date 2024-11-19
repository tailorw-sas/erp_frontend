package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.AttachmentDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "attachment")
public class Attachment implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(columnDefinition = "serial", name = "attachment_gen_id")
    @Generated(event = EventType.INSERT)
    private Long attachmentId;

    private String filename;

    private String employee;

    private UUID employeeId;

    private String file;

    private String remark;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_attachment_type")
    private ManageAttachmentType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction")
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resource_type")
    private ManageResourceType paymentResourceType;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Attachment(AttachmentDto dto) {
        this.id = dto.getId();
        this.attachmentId = dto.getAttachmentId();
        this.transaction = dto.getTransaction() != null ? new Transaction(dto.getTransaction()) : null;
        this.filename = dto.getFilename();
        this.file = dto.getFile();
        this.remark = dto.getRemark();
        this.type = dto.getType() != null ? new ManageAttachmentType(dto.getType()) : null;
        this.employee = dto.getEmployee();
        this.employeeId = dto.getEmployeeId();
        this.paymentResourceType = dto.getPaymentResourceType() != null ? new ManageResourceType(dto.getPaymentResourceType()) : null;
    }

    public AttachmentDto toAggregate() {
        return new AttachmentDto(
                id, 
                attachmentId, 
                filename, 
                file, 
                remark,
                type != null ? type.toAggregate() : null,
                transaction != null ? transaction.toAggregateParent() : null,
                employee, 
                employeeId, 
                createdAt, 
                paymentResourceType != null ? paymentResourceType.toAggregate() : null
        );
    }

    public AttachmentDto toAggregateSimple() {
        return new AttachmentDto(
                id, 
                attachmentId, 
                filename, 
                file, 
                remark,
                type != null ? type.toAggregate() : null,
                null,
                employee, 
                employeeId, 
                createdAt, 
                paymentResourceType != null ? paymentResourceType.toAggregate() : null
        );
    }
}
