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
@Table(name = "room_rate")
public class RoomRate {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RoomRateStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id", nullable = true)
    private ManageHotel hotel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(name = "agency_code")
    private String agencyCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agency_id")
    private ManageAgency agency;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "stay_days")
    private int stayDays;

    @Column(name = "reservation_code")
    private String reservationCode;

    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "room_type_code")
    private String roomTypeCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_type_id")
    private ManageRoomType roomType;

    @Column(name = "coupon_number")
    private String couponNumber;

    @Column(name = "total_number_of_guest")
    private int totalNumberOfGuest;

    @Column(name = "adults")
    private int adults;

    @Column(name = "children")
    private int children;

    @Column(name = "rate_plan_code")
    private String ratePlanCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rate_plan_id")
    private ManageRatePlan ratePlan;

    @Column(name = "invoice_date")
    private LocalDate invoicingDate;

    @Column(name = "hotel_creation_date")
    private LocalDate hotelCreationDate;

    @Column(name = "original_amount")
    private Double originalAmount;

    @Column(name = "amount_payment_applied")
    private Double amountPaymentApplied;

    @Column(name = "rate_by_adult")
    private Double rateByAdult;

    @Column(name = "rate_by_child")
    private Double rateByChild;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "hotel_invoice_amount")
    private Double hotelInvoiceAmount;

    @Column(name = "hotel_invoice_number")
    private String hotelInvoiceNumber;

    @Column(name = "invoice_folio_number")
    private String invoiceFolioNumber;

    @Column(name = "quote")
    private Double quote;

    @Column(name = "renewal_number")
    private String renewalNumber;

    @Column(name = "room_category_code")
    private String roomCategoryCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_category_id")
    private ManageRoomCategory roomCategory;

    @Column(name = "hash")
    private String hash;

    @Column(name = "invoice_id")
    private UUID invoiceId;

    @Column(name = "booking_id")
    private UUID bookingId;

    public RoomRate(RoomRateDto dto){
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
        this.children = dto.getChildren();
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
        this.invoiceId = dto.getInvoiceId();
        this.bookingId = dto.getBookingId();
    }

    public RoomRateDto toAggregate(){
        return new RoomRateDto(
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
                Objects.nonNull(this.roomCategory) ? this.roomType.toAggregate() : null,
                this.couponNumber,
                this.totalNumberOfGuest,
                this.adults,
                this.children,
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
                this.hash,
                this.roomCategoryCode,
                Objects.nonNull(this.roomCategory) ? this.roomCategory.toAggregate() : null,
                Objects.isNull(this.agency) ? "Agency doesn´t exist" : null,
                this.invoiceId,
                this.bookingId
        );
    }
}
