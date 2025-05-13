package com.kynsoft.finamer.insis.infrastructure.model;

import com.kynsoft.finamer.insis.domain.dto.RoomRateDto;
import com.kynsoft.finamer.insis.infrastructure.model.enums.RoomRateStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "roomRate")
public class RoomRate {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private RoomRateStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id", nullable = true)
    private ManageHotel hotel;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    private String agency;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int stayDays;
    private String reservationCode;
    private String guestName;
    private String firstName;
    private String lastName;
    private Double amount;
    private String roomType;
    private String couponNumber;
    private int totalNumberOfGuest;
    private int adults;
    private int childrens;
    private String ratePlan;
    private LocalDate invoicingDate;
    private LocalDate hotelCreationDate;
    private Double originalAmount;
    private Double amountPaymentApplied;
    private Double rateByAdult;
    private Double rateByChild;
    private String remarks;
    private String roomNumber;
    private Double hotelInvoiceAmount;
    private String hotelInvoiceNumber;
    private String invoiceFolioNumber;
    private Double quote;
    private String renewalNumber;
    private String hash;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    public RoomRate(RoomRateDto dto){
        this.id = dto.getId();
        this.hotel = Objects.nonNull(dto.getHotel()) ? new ManageHotel(dto.getHotel()) : null;
        this.updatedAt = dto.getUpdatedAt();
        this.agency = dto.getAgency();
        this.checkInDate = dto.getCheckInDate();
        this.checkOutDate = dto.getCheckOutDate();
        this.stayDays = dto.getStayDays();
        this.reservationCode = dto.getReservationCode();
        this.guestName = dto.getGuestName();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.amount = dto.getAmount();
        this.roomType = dto.getRoomType();
        this.couponNumber = dto.getCouponNumber();
        this.totalNumberOfGuest = dto.getTotalNumberOfGuest();
        this.adults = dto.getAdults();
        this.childrens = dto.getChildrens();
        this.ratePlan = dto.getRatePlan();
        this.invoicingDate = dto.getInvoicingDate();
        this.hotelCreationDate = dto.getHotelCreationDate();
        this.originalAmount = dto.getOriginalAmount();
        this.amountPaymentApplied = dto.getAmountPaymentApplied();
        this.rateByAdult = dto.getRateByAdult();
        this.rateByChild = dto.getRateByChild();
        this.remarks = dto.getRemarks();
        this.roomNumber = dto.getRoomNumber();
        this.hotelInvoiceAmount = dto.getHotelInvoiceAmount();
        this.hotelInvoiceNumber = dto.getHotelInvoiceNumber();
        this.invoiceFolioNumber = dto.getInvoiceFolioNumber();
        this.quote = dto.getQuote();
        this.renewalNumber = dto.getRenewalNumber();
        this.hash = dto.getHash();
        this.booking = Objects.nonNull(dto.getBooking()) ? new Booking(dto.getBooking()) : null;
    }

    public RoomRateDto toAggregate(){
        return new RoomRateDto(
                this.id,
                this.status,
                Objects.nonNull(this.hotel) ? this.hotel.toAggregate() : null,
                this.updatedAt,
                this.agency,
                this.checkInDate,
                this.checkOutDate,
                this.stayDays,
                this.reservationCode,
                this.guestName,
                this.firstName,
                this.lastName,
                this.amount,
                this.roomType,
                this.couponNumber,
                this.totalNumberOfGuest,
                this.adults,
                this.childrens,
                this.ratePlan,
                this.invoicingDate,
                this.hotelCreationDate,
                this.originalAmount,
                this.amountPaymentApplied,
                this.rateByAdult,
                this.rateByChild,
                this.remarks,
                this.roomNumber,
                this.hotelInvoiceAmount,
                this.hotelInvoiceNumber,
                this.invoiceFolioNumber,
                this.quote,
                this.renewalNumber,
                this.hash,
                Objects.nonNull(this.booking) ? this.booking.toAggregate() : null
        );
    }
}
