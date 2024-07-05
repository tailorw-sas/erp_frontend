package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageRatePlanDto;
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
@Table(name = "manage_rate_plan")
public class ManageRatePlan implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String code;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id")
    private ManageHotel hotel;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    public ManageRatePlan(ManageRatePlanDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.hotel = new ManageHotel(dto.getHotel());
        this.description = dto.getDescription();
        this.status = dto.getStatus();
    }

    public ManageRatePlanDto toAggregate() {
        return new ManageRatePlanDto(id, code, name,
                hotel != null ? hotel.toAggregate() : null, description, status);
    }

}