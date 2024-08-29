package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManagerPaymentStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_payment_status")
public class ManagerPaymentStatus {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String code;
    @Column(unique = true)
    private String name;
    @Column
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column
    private Boolean collected;
    @Column(name = "description")
    private String description;

    private Boolean defaults;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean applied;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public ManagerPaymentStatus(ManagerPaymentStatusDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.collected = dto.getCollected();
        this.description = dto.getDescription();
        this.defaults = dto.getDefaults();
        this.applied = dto.getApplied();
    }
    
    public ManagerPaymentStatusDto toAggregate() {
        return new ManagerPaymentStatusDto(
                id, 
                code, 
                name, 
                status, 
                collected, 
                description, 
                defaults != null ? defaults : null,
                applied
        );
    }
}
