package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomTypeDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "manage_room_type")
public class ManageRoomType implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    //    @Column(unique = true)
    private String code;

    @Column(nullable = true)
    private Boolean deleted = false;

    private String status;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id")
    private ManageHotel hotel;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deletedAt;

    public ManageRoomType(ManageRoomTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.hotel = dto.getHotel() != null ? new ManageHotel(dto.getHotel()) : null;
    }

    public ManageRoomTypeDto toAggregate() {
        ManageHotelDto hotel = this.hotel != null ? this.hotel.toAggregate() : null;
        return new ManageRoomTypeDto(
                id, code, name, status, hotel
        );
    }

}
