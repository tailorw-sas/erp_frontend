package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "master_payment_attachment")
public class MasterPaymentAttachment implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition="serial", name = "attachment_gen_id")
    @Generated(event = EventType.INSERT)
    private Long attachmentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_id")
    private Payment resource;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resource_type_id")
    private ResourceType resourceType;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "attachment_type_id")
    private AttachmentType attachmentType;

    private String fileName;
    private String fileWeight;
    private String path;
    private String remark;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public MasterPaymentAttachment(MasterPaymentAttachmentDto dto) {
        this.id = dto.getId();
        this.resource = dto.getResource() != null ? new Payment(dto.getResource()) : null;
        this.resourceType = dto.getResourceType() != null ? new ResourceType(dto.getResourceType()) : null;
        this.attachmentType = dto.getAttachmentType() != null ? new AttachmentType(dto.getAttachmentType()) : null;
        this.fileName = dto.getFileName();
        this.fileWeight = dto.getFileWeight();
        this.path = dto.getPath();
        this.status = dto.getStatus();
        this.remark = dto.getRemark();
    }

    public MasterPaymentAttachmentDto toAggregate() {
        return new MasterPaymentAttachmentDto(
                id, 
                status, 
                resource.toAggregate(), 
                resourceType != null ? resourceType.toAggregate() : null, 
                attachmentType != null ? attachmentType.toAggregate() : null, 
                fileName, 
                fileWeight != null ? fileWeight : null,
                path, 
                remark,
                attachmentId
        );
    }

    public MasterPaymentAttachmentDto toAggregateSimple() {
        return new MasterPaymentAttachmentDto(
                id, 
                status, 
                null, 
                resourceType != null ? resourceType.toAggregate() : null, 
                attachmentType != null ? attachmentType.toAggregate() : null, 
                fileName, 
                fileWeight != null ? fileWeight : null,
                path, 
                remark,
                attachmentId
        );
    }
}
