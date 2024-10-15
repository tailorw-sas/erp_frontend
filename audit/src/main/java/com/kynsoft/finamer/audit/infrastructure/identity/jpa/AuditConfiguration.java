package com.kynsoft.finamer.audit.infrastructure.identity.jpa;

import com.kynsoft.finamer.audit.domain.dto.AuditConfigurationDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="audit_register")
public class AuditConfiguration implements Serializable {
    @Id
    @Column(name="id")
    private UUID id;
    private boolean auditUpdate;
    private boolean auditCreate;
    private boolean auditDelete;
    private String serviceName;
    private String entityName;

    public AuditConfigurationDto toAggregate(){
        return  new AuditConfigurationDto(id,auditCreate,auditUpdate,auditDelete,serviceName,entityName);
    }
}