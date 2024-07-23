package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "manage_hotel")
public class ManageHotel implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    private String code;

    private String name;

    private Boolean isApplyByVcc;

    private String status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public ManageHotel(ManageHotelDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.isApplyByVcc = dto.getIsApplyByVCC();
        this.status = dto.getStatus();
    }

    public ManageHotelDto toAggregate(){
        return new ManageHotelDto(
                id,
                code,
                name,
                isApplyByVcc,
                status
        );
    }
}
