package com.kynsoft.finamer.insis.infrastructure.model;

import com.kynsoft.finamer.insis.domain.dto.ManageRatePlanDto;
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
@Table(name = "manage_rate_plan")
public class ManageRatePlan implements Serializable {
    @Id
    private UUID id;

    private String code;

    private String name;

    private String status;

    private boolean deleted;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id")
    private ManageHotel hotel;

    public ManageRatePlan(ManageRatePlanDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.deleted = dto.isDeleted();
        this.updatedAt = dto.getUpdatedAt();
        this.hotel = new ManageHotel(dto.getHotel());
    }

    public ManageRatePlanDto toAggregate(){
        return new ManageRatePlanDto(
                id,
                code,
                name,
                status,
                deleted,
                updatedAt,
                hotel!= null ? hotel.toAggregate() : null
        );
    }
}
