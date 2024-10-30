package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentSourceDto;
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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_payment_source")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_payment_source",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManagePaymentSource implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String description;

    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    private Boolean isBank;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean expense;

    public ManagePaymentSource(ManagePaymentSourceDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.name = dto.getName();
        this.isBank = dto.getIsBank();
        this.expense = dto.getExpense();
    }

    public ManagePaymentSourceDto toAggregate(){
        return new ManagePaymentSourceDto(id, code, description, status, name, isBank, expense);
    }
}
