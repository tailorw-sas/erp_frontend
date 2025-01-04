package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
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
@Table(name = "manage_attachment_type")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_attachment_type",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageAttachmentType implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;
    private String name;
    private String description;
    private Boolean defaults;
    @Column(name = "anti_import",columnDefinition = "boolean default false")
    private boolean antiToIncomeImport;

    @Enumerated(EnumType.STRING)
    private Status status;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public ManageAttachmentType(AttachmentTypeDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.defaults = dto.getDefaults();
        this.status = dto.getStatus();
        this.antiToIncomeImport=dto.isAntiToIncomeImport();
    }

    public AttachmentTypeDto toAggregate() {
        return new AttachmentTypeDto(id, code, name, description, defaults, status,antiToIncomeImport);
    }

}
