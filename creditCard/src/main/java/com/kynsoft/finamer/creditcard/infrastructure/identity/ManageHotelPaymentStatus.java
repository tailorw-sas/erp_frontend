package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelPaymentStatusDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
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
@Table(name = "manage_hotel_payment_status")
public class ManageHotelPaymentStatus implements Serializable {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code;

    private String name;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String description;

    private boolean inProgress;

    private boolean completed;

    private boolean cancelled;

    private boolean applied;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ManageHotelPaymentStatus(ManageHotelPaymentStatusDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.inProgress = dto.isInProgress();
        this.completed = dto.isCompleted();
        this.cancelled = dto.isCancelled();
        this.applied = dto.isApplied();
    }

    public ManageHotelPaymentStatusDto toAggregate(){
        return new ManageHotelPaymentStatusDto(
                id, code, name, status, description, inProgress, completed, cancelled, applied
        );
    }
}
