package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(columnDefinition = "serial", name = "booking_gen_id")
    @Generated(event = EventType.INSERT)
    private Long bookingId;

    @Column(columnDefinition = "serial", name = "reservation_number")
    @Generated(event = EventType.INSERT)
    private Long reservationNumber;

    private LocalDateTime hotelCreationDate;
    private LocalDateTime bookingDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    private String hotelBookingNumber;
    private String fullName;
    private String firstName;
    private String lastName;
    private Double invoiceAmount;
    private Double dueAmount;
    private String roomNumber;
    private String couponNumber;
    private Integer adults;
    private Long nights;
    private Integer children;
    private Double rateAdult;
    private Double rateChild;
    private String hotelInvoiceNumber;
    private String folioNumber;
    private Double hotelAmount;
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_invoice", nullable = true)
    @JsonBackReference
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_rate_plan", nullable = true)
    private ManageRatePlan ratePlan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_night_type", nullable = true)
    private ManageNightType nightType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_room_type", nullable = true)
    private ManageRoomType roomType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_room_category", nullable = true)
    private ManageRoomCategory roomCategory;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "booking")
    @JsonManagedReference
    private List<ManageRoomRate> roomRates;

    @Column(nullable = true)
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Booking parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "manageBooking")
    private List<PaymentDetail> paymentDetails;

    private String contract;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean deleteInvoice;

    public Booking(ManageBookingDto dto) {
        this.id = dto.getId();
        this.hotelCreationDate = dto.getHotelCreationDate();
        this.bookingDate = dto.getBookingDate();
        this.checkIn = dto.getCheckIn();
        this.checkOut = dto.getCheckOut();
        this.hotelBookingNumber = dto.getHotelBookingNumber();
        this.fullName = dto.getFullName();
        this.invoiceAmount = Objects.nonNull(dto.getInvoiceAmount()) ? BankerRounding.round(dto.getInvoiceAmount()) : null;
        this.roomNumber = dto.getRoomNumber();
        this.couponNumber = dto.getCouponNumber();
        this.adults = dto.getAdults();
        this.children = Objects.nonNull(dto.getChildren()) ? dto.getChildren() : 0;
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.rateAdult = Objects.nonNull(dto.getRateAdult()) ? BankerRounding.round(dto.getRateAdult()) : null;
        this.rateChild = Objects.nonNull(dto.getRateChild()) ? BankerRounding.round(dto.getRateChild()) : null;
        this.hotelInvoiceNumber = dto.getHotelInvoiceNumber();
        this.folioNumber = dto.getFolioNumber();
        this.hotelAmount = Objects.nonNull(dto.getHotelAmount()) ? BankerRounding.round(dto.getHotelAmount()) : null;
        this.description = dto.getDescription();
        this.invoice = Objects.nonNull(dto.getInvoice()) ? new Invoice(dto.getInvoice()) : null;
        this.ratePlan = Objects.nonNull(dto.getRatePlan()) ? new ManageRatePlan(dto.getRatePlan()) : null;
        this.nightType = Objects.nonNull(dto.getNightType()) ? new ManageNightType(dto.getNightType()) : null;
        this.roomType = Objects.nonNull(dto.getRoomType()) ? new ManageRoomType(dto.getRoomType()) : null;
        this.roomCategory = Objects.nonNull(dto.getRoomCategory()) ? new ManageRoomCategory(dto.getRoomCategory()) : null;
        this.roomRates = Objects.nonNull(dto.getRoomRates()) ? dto.getRoomRates().stream().map(r -> {
            r.setBooking(null);
            ManageRoomRate roomRate = new ManageRoomRate(r);
            roomRate.setRoomRateId(Objects.nonNull(this.roomRates) ? this.roomRates.size() + 1L : 1L);
            roomRate.setBooking(this);
            return roomRate;
        }).collect(Collectors.toList()) : null;

        this.nights = Objects.nonNull(dto.getCheckIn()) && Objects.nonNull(dto.getCheckOut()) ? dto.getCheckIn().until(dto.getCheckOut(), ChronoUnit.DAYS) : 0L;
        this.dueAmount = Objects.nonNull(dto.getDueAmount()) ? BankerRounding.round(dto.getDueAmount()) : 0.0;
        this.parent = Objects.nonNull(dto.getParent()) ? new Booking(dto.getParent()) : null;
        this.contract = dto.getContract();
        this.deleteInvoice = dto.isDeleteInvoice();
        this.updatedAt = dto.getUpdatedAt();
    }

    public ManageBookingDto toAggregate() {
        return new ManageBookingDto(
                this.id,
                this.bookingId,
                this.reservationNumber,
                this.hotelCreationDate,
                this.bookingDate,
                this.checkIn,
                this.checkOut,
                this.hotelBookingNumber,
                this.fullName,
                this.firstName,
                this.lastName,
                Objects.nonNull(this.invoiceAmount) ? BankerRounding.round(this.invoiceAmount) : null,
                Objects.nonNull(this.dueAmount) ? BankerRounding.round(this.dueAmount) : 0.0,
                this.roomNumber,
                this.couponNumber,
                this.adults,
                Objects.nonNull(this.children) ? this.children : 0,
                Objects.nonNull(this.rateAdult) ? BankerRounding.round(this.rateAdult) : null,
                Objects.nonNull(this.rateChild) ? BankerRounding.round(this.rateChild) : null,
                this.hotelInvoiceNumber,
                this.folioNumber,
                Objects.nonNull(this.hotelAmount) ? BankerRounding.round(this.hotelAmount) : null,
                this.description,
                Objects.nonNull(this.invoice) ? this.invoice.toAggregateSimple() : null,
                Objects.nonNull(this.ratePlan) ? this.ratePlan.toAggregate() : null,
                Objects.nonNull(this.nightType) ? this.nightType.toAggregate() : null,
                Objects.nonNull(this.roomType) ? this.roomType.toAggregate() : null,
                Objects.nonNull(this.roomCategory) ? this.roomCategory.toAggregate() : null,
                null,
                this.nights,
                Objects.nonNull(this.parent) ? this.parent.toAggregateSimple() : null,
                this.contract,
                this.deleteInvoice,
                this.updatedAt
        );

    }

    public ManageBookingDto toAggregateWithRates() {
        return new ManageBookingDto(
                this.id,
                this.bookingId,
                this.reservationNumber,
                this.hotelCreationDate,
                this.bookingDate,
                this.checkIn,
                this.checkOut,
                this.hotelBookingNumber,
                this.fullName,
                this.firstName,
                this.lastName,
                Objects.nonNull(this.invoiceAmount) ? BankerRounding.round(this.invoiceAmount) : null,
                Objects.nonNull(this.dueAmount) ? BankerRounding.round(this.dueAmount) : 0.0,
                this.roomNumber,
                this.couponNumber,
                this.adults,
                Objects.nonNull(this.children) ? this.children : 0,
                Objects.nonNull(this.rateAdult) ? BankerRounding.round(this.rateAdult) : null,
                Objects.nonNull(this.rateChild) ? BankerRounding.round(this.rateChild) : null,
                this.hotelInvoiceNumber, this.folioNumber,
                Objects.nonNull(this.hotelAmount) ? BankerRounding.round(this.hotelAmount) : null,
                this.description,
                Objects.nonNull(this.invoice) ? this.invoice.toAggregateSimple() : null,
                Objects.nonNull(this.ratePlan) ? this.ratePlan.toAggregate() : null,
                Objects.nonNull(this.nightType) ? this.nightType.toAggregate() : null,
                Objects.nonNull(this.roomType) ? this.roomType.toAggregate() : null,
                Objects.nonNull(this.roomCategory) ? this.roomCategory.toAggregate() : null,
                Objects.nonNull(this.roomRates) ? this.roomRates.stream().map(ManageRoomRate::toAggregateSimple).collect(Collectors.toList()) : null,
                this.nights,
                Objects.nonNull(this.parent) ? this.parent.toAggregateSimple() : null,
                this.contract,
                this.deleteInvoice,
                this.updatedAt
        );
    }

    public ManageBookingDto toAggregateSimple() {
        return new ManageBookingDto(
                this.id,
                this.bookingId,
                this.reservationNumber,
                this.hotelCreationDate,
                this.bookingDate,
                this.checkIn,
                this.checkOut,
                this.hotelBookingNumber,
                this.fullName,
                this.firstName,
                this.lastName,
                Objects.nonNull(this.invoiceAmount) ? BankerRounding.round(this.invoiceAmount) : null,
                Objects.nonNull(this.dueAmount) ? BankerRounding.round(this.dueAmount) : null,
                this.roomNumber,
                this.couponNumber,
                this.adults,
                this.children,
                Objects.nonNull(this.rateAdult) ? BankerRounding.round(this.rateAdult) : null,
                Objects.nonNull(this.rateChild) ? BankerRounding.round(this.rateChild) : null,
                this.hotelInvoiceNumber,
                this.folioNumber,
                Objects.nonNull(this.hotelAmount) ? BankerRounding.round(this.hotelAmount) : null,
                this.description,
                null,
                Objects.nonNull(this.ratePlan) ? this.ratePlan.toAggregate() : null,
                Objects.nonNull(this.nightType) ? this.nightType.toAggregate() : null,
                Objects.nonNull(this.roomType) ? this.roomType.toAggregate() : null,
                Objects.nonNull(this.roomCategory) ? this.roomCategory.toAggregate() : null,
                null,
                this.nights,
                null,
                this.contract,
                this.deleteInvoice,
                this.updatedAt
        );
    }

    @PostLoad
    public void initDefaultValue() {
        if (Objects.isNull(this.dueAmount)) {
            this.dueAmount = 0.0;
        }
    }
}
