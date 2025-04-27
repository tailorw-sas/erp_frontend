package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.settings.domain.dto.ManageB2BPartnerTypeDto;
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
@Table(name = "manage_b2b_partner_type")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_b2b_partner_type",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageB2BPartnerType implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String code;
    
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    public ManageB2BPartnerType(ManageB2BPartnerTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
    }

    public ManageB2BPartnerTypeDto toAggregate() {
        return new ManageB2BPartnerTypeDto(id, code, name, description, status);
    }

    public ManageB2BPartnerType(UUID id,
                                String code,
                                String name,
                                String description,
                                Status status){
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.status = status;
    }
}
