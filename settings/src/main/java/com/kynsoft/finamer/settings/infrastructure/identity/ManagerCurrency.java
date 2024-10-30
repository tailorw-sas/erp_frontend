package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.settings.domain.dto.ManagerCurrencyDto;
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
@Table(name = "manager_currency")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manager_currency",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManagerCurrency implements Serializable {

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

    public ManagerCurrency(ManagerCurrencyDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
    }

    public ManagerCurrencyDto toAggregate() {
        return new ManagerCurrencyDto(id, code, name, description, status);
    }

}