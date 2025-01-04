package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyTypeDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

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
    private String code;
    private String status;
    private String name;

    public ManageAgencyType(ManageAgencyTypeDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.name = dto.getName();
    }

    public ManageAgencyTypeDto toAggregate(){
        return new ManageAgencyTypeDto(
                id, code, status, name
        );
    }
}
