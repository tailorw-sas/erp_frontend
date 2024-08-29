package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

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


    @Column(columnDefinition = "serial", name = "room_rate_serial_id")
    @Generated(event = EventType.INSERT)
    private Long roomRateId;

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
    private Long nights;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_booking")
    private ManageBooking booking;

    @Column(nullable = true)
    private Boolean deleted = false;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "roomRate")
    private List<ManageAdjustment> adjustments;

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
        this.roomRateId = dto.getRoomRateId();
        this.invoiceAmount = dto.getInvoiceAmount();
        this.roomNumber = dto.getRoomNumber();

        this.adults = dto.getAdults();
        this.children = dto.getChildren();
        this.rateAdult = dto.getRateAdult();
        this.rateChild = dto.getRateChild();

        this.remark = dto.getRemark();
        this.hotelAmount = dto.getHotelAmount();

        this.booking = dto.getBooking() != null ? new ManageBooking(dto.getBooking()) : null;
        this.adjustments = dto.getAdjustments() != null ? dto.getAdjustments().stream().map(a -> {
            ManageAdjustment adjustment = new ManageAdjustment(a);
            adjustment.setRoomRate(this);
            return adjustment;
        }).collect(Collectors.toList()) : null;

        this.nights = dto.getCheckIn() != null && dto.getCheckOut() !=null ? dto.getCheckIn().until(dto.getCheckOut(), ChronoUnit.DAYS) : 0L;
    }

    public ManageRoomRateDto toAggregate() {
        return new ManageRoomRateDto(id, roomRateId, checkIn, checkOut, invoiceAmount, roomNumber, adults, children,
                rateAdult, rateChild, hotelAmount, remark, booking !=null ?  booking.toAggregate() : null,
                adjustments != null ? adjustments.stream().map(b -> {
                    return b.toAggregateSample();
                }).collect(Collectors.toList()) : null, nights);
    }

    public ManageRoomRateDto toAggregateSample() {
        return new ManageRoomRateDto(id, roomRateId, checkIn, checkOut, invoiceAmount, roomNumber, adults, children,
                rateAdult, rateChild, hotelAmount, remark, null,
                adjustments != null ? adjustments.stream().map(b -> {
                    return b.toAggregateSample();
                }).collect(Collectors.toList()) : null, nights);
    }
}
