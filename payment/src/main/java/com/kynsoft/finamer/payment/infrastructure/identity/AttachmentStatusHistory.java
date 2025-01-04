package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.AttachmentStatusHistoryDto;
import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
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
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "attachment_status_history",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class AttachmentStatusHistory implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Status status;
    private String attachmentStatus;
    private Long attachmentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private ManageEmployee employee;

    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public AttachmentStatusHistory(AttachmentStatusHistoryDto dto) {
        this.id = dto.getId();
        this.payment = dto.getPayment() != null ? new Payment(dto.getPayment()) : null;
        this.employee = dto.getEmployee() != null ? new ManageEmployee(dto.getEmployee()) : null;
        this.description = dto.getDescription();
        this.attachmentStatus = dto.getStatus();
        this.attachmentId = dto.getAttachmentId();
    }

    public AttachmentStatusHistoryDto toAggregate(){
        return new AttachmentStatusHistoryDto(
                id, 
                attachmentStatus, 
                payment != null ? payment.toAggregate() : null, 
                employee != null ? employee.toAggregate() : null, 
                description, 
                attachmentId,
                createdAt, 
                updatedAt
        );
    }
}
