package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.creditcard.domain.dto.ParameterizationDto;
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
@Table(name = "parameterization")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "parameterization",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class Parameterization implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    private Boolean isActive;

    @Column(columnDefinition = "int DEFAULT 2")
    private int decimals = 2;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Parameterization(ParameterizationDto dto){
        this.id = dto.getId();
        this.isActive = dto.getIsActive();
        this.decimals = dto.getDecimals();
    }

    public ParameterizationDto toAggregate(){
        return new ParameterizationDto(
                id, isActive, decimals
        );
    }

}
