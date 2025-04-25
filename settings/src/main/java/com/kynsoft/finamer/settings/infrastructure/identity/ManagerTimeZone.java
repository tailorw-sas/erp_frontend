package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.settings.domain.dto.ManagerTimeZoneDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manager_time_zone")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manager_time_zone",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManagerTimeZone implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String code;

    private String name;
    private String description;
    private Double elapse;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    public ManagerTimeZone(ManagerTimeZoneDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.elapse = dto.getElapse();
        this.status = dto.getStatus();
    }

    public ManagerTimeZoneDto toAggregate() {
        return new ManagerTimeZoneDto(id, code, name, description, elapse, status);
    }

    public ManagerTimeZone(UUID id,
                           String code,
                           String name,
                           String description,
                           Double elapse,
                           Status status){
        this.id = id;
        this.code = code;
        this.description = description;
        this.name = name;
        this.elapse = elapse;
        this.status = status;
    }

}