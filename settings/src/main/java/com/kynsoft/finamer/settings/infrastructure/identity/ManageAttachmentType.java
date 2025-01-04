package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.settings.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
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

    @Enumerated(EnumType.STRING)
    private Status status;

    private String description;

    private String name;
    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean defaults;
    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean attachInvDefault;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public ManageAttachmentType(ManageAttachmentTypeDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.name = dto.getName();
        this.defaults = dto.getDefaults();
        this.attachInvDefault = dto.getAttachInvDefault();
    }

    public ManageAttachmentTypeDto toAggregate(){
        return new ManageAttachmentTypeDto(
                id, code, description, status, name, defaults, attachInvDefault
        );
    }

}
