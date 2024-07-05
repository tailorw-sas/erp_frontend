package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_room_rate")
public class ManageRoomRate {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(columnDefinition="serial", name = "room_rate_gen_id")
    @Generated(GenerationTime.INSERT)
    private Long room_rate_id;


    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Double invoiceAmount;
    private String roomNumber;
    private Integer adults;
    private Integer children;
    private Double rateAdult;
    private Double rateChild;
    private Double hotelAmount;
    private String remark;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_booking" )
    private ManageBooking booking;

    @Column(nullable = true)
    private Boolean deleted = false;



    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deletedAt;



    public ManageRoomRate(ManageRoomRateDto dto) {
        this.id = dto.getId();


        this.checkIn = dto.getCheckIn();
        this.checkOut = dto.getCheckOut();



        this.invoiceAmount = dto.getInvoiceAmount();
        this.roomNumber = dto.getRoomNumber();

        this.adults = dto.getAdults();
        this.children = dto.getChildren();
        this.rateAdult = dto.getRateAdult();
        this.rateChild = dto.getRateChild();

        this.remark = dto.getRemark();
        this.hotelAmount = dto.getHotelAmount();


        this.booking = dto.getBooking() != null ? new ManageBooking(dto.getBooking()): null ;
    }

    public ManageRoomRateDto toAggregate() {
        return new ManageRoomRateDto(id,room_rate_id, checkIn, checkOut, invoiceAmount, roomNumber, adults, children, rateAdult, rateChild,hotelAmount,remark, booking.toAggregate());
    }
}
