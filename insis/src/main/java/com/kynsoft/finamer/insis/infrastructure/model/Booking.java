package com.kynsoft.finamer.insis.infrastructure.model;

import com.kynsoft.finamer.insis.domain.dto.BookingDto;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BookingStatus;
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
@Table(name = "booking")
public class Booking {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id", nullable = true)
    private ManageHotel hotel;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    private String agencyCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agency_id")
    private ManageAgency agency;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int stayDays;
    private String reservationCode;
    private String guestName;
    private String firstName;
    private String lastName;
    private Double amount;
    private String roomTypeCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roomType_id")
    private ManageRoomType roomType;

    private String couponNumber;
    private int totalNumberOfGuest;
    private int adults;
    private int childrens;
    private String ratePlanCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ratePlan_id")
    private ManageRatePlan ratePlan;

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

    private String roomCategoryCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roomCategory_id")
    private ManageRoomCategory roomCategory;

    private String hash;

    public Booking(BookingDto dto){
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.hotel = Objects.nonNull(dto.getHotel()) ? new ManageHotel(dto.getHotel()) : null;
        this.updatedAt = dto.getUpdatedAt();
        this.agencyCode = dto.getAgencyCode();
        this.agency = Objects.nonNull(dto.getAgency()) ? new ManageAgency(dto.getAgency()) : null;
        this.checkInDate = dto.getCheckInDate();
        this.checkOutDate = dto.getCheckOutDate();
        this.stayDays = dto.getStayDays();
        this.reservationCode = dto.getReservationCode();
        this.guestName = dto.getGuestName();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.amount = dto.getAmount();
        this.roomTypeCode = dto.getRoomTypeCode();
        this.roomType = Objects.nonNull(dto.getRoomType()) ? new ManageRoomType(dto.getRoomType()) : null;
        this.couponNumber = dto.getCouponNumber();
        this.totalNumberOfGuest = dto.getTotalNumberOfGuest();
        this.adults = dto.getAdults();
        this.childrens = dto.getChildrens();
        this.ratePlanCode = dto.getRatePlanCode();
        this.ratePlan = Objects.nonNull(dto.getRatePlan()) ? new ManageRatePlan(dto.getRatePlan()) : null;
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
        this.roomCategoryCode = dto.getRoomCategoryCode();
        this.roomCategory = Objects.nonNull(dto.getRoomCategory()) ? new ManageRoomCategory(dto.getRoomCategory()) : null;
        this.hash = dto.getHash();
    }

    public BookingDto toAggregate(){
        return new BookingDto(
                this.id,
                this.status,
                Objects.nonNull(this.hotel) ? this.hotel.toAggregate() : null,
                this.updatedAt,
                this.agencyCode,
                Objects.nonNull(this.agency) ? this.agency.toAggregate() : null,
                this.checkInDate,
                this.checkOutDate,
                this.stayDays,
                this.reservationCode,
                this.guestName,
                this.firstName,
                this.lastName,
                this.amount,
                this.roomTypeCode,
                Objects.nonNull(this.roomType) ? this.roomType.toAggregate() : null,
                this.couponNumber,
                this.totalNumberOfGuest,
                this.adults,
                this.childrens,
                this.ratePlanCode,
                Objects.nonNull(this.ratePlan) ? this.ratePlan.toAggregate() : null,
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
                this.roomCategoryCode,
                Objects.nonNull(this.roomCategory) ? this.getRoomCategory().toAggregate() : null,
                this.hash,
                Objects.isNull(this.agency) ? "Agency doesn´t exist" : null
        );
    }
}
