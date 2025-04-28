package com.kynsoft.finamer.settings.infrastructure.identity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_agency_type")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_agency_type",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageAgencyType {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public ManageAgencyType(ManageAgencyTypeDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.name = dto.getName();
        this.description = dto.getDescription();
    }

    public ManageAgencyTypeDto toAggregate(){
        return new ManageAgencyTypeDto(
                id, code, status, name, description
        );
    }

    public ManageAgencyType(UUID id,
                            String code,
                            Status status,
                            String name,
                            String description){
        this.id = id;
        this.code = code;
        this.status = status;
        this.name = name;
        this.description = description;
    }
}
