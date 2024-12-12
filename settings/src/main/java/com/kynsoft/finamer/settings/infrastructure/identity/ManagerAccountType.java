package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.settings.domain.dto.ManagerAccountTypeDto;
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
@Table(name = "manager_account_type")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manager_account_type",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManagerAccountType implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String code;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean moduleVcc;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean modulePayment;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    public ManagerAccountType(ManagerAccountTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.moduleVcc = dto.isModuleVcc();
        this.modulePayment = dto.isModulePayment();
    }

    public ManagerAccountTypeDto toAggregate() {
        return new ManagerAccountTypeDto(id, code, name, description, status, moduleVcc, modulePayment);
    }

}