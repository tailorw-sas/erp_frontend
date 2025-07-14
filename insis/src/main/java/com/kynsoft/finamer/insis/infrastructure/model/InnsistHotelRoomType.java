package com.kynsoft.finamer.insis.infrastructure.model;

import com.kynsoft.finamer.insis.domain.dto.InnsistHotelRoomTypeDto;
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
@Table(name = "innsist_hotel_room_type")
public class InnsistHotelRoomType implements Serializable {
    @Id
    public UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id")
    public ManageHotel hotel;

    @Column(name = "room_type_prefix")
    private String roomTypePrefix;

    @Column(name = "status")
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(name = "deleted")
    private boolean deleted;

    public InnsistHotelRoomType(InnsistHotelRoomTypeDto dto){
        this.id = dto.getId();
        this.hotel = new ManageHotel(dto.getHotel());
        this.roomTypePrefix = dto.getRoomTypePrefix();
        this.status = dto.getStatus();
        this.updatedAt = dto.getUpdatedAt();
        this.deleted = dto.isDeleted();
    }

    public InnsistHotelRoomTypeDto toAggregate(){
        return new InnsistHotelRoomTypeDto(
                id,
                hotel!=null ? hotel.toAggregate() : null,
                roomTypePrefix,
                status,
                updatedAt,
                deleted
        );
    }
}
