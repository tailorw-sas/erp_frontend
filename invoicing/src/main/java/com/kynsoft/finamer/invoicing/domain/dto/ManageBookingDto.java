package com.kynsoft.finamer.invoicing.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageBookingDto {

    private UUID id;

    private Long bookingId;

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
    private Integer children;
    private Double rateAdult;
    private Double rateChild;
    private String hotelInvoiceNumber;
    private String folioNumber;
    private Double hotelAmount;
    private String description;
    private ManageInvoiceDto invoice;
    private ManageRatePlanDto ratePlan;
    private ManageNightTypeDto nightType;
    private ManageRoomTypeDto roomType;
    private ManageRoomCategoryDto roomCategory;
    private List<ManageRoomRateDto> roomRates;
    private Long nights;

    private ManageBookingDto parent;

    public ManageBookingDto(ManageBookingDto dto) {
        this.id = UUID.randomUUID();
        this.bookingId = dto.getBookingId();
        this.reservationNumber = dto.getReservationNumber();
        this.hotelCreationDate = dto.getHotelCreationDate();
        this.bookingDate = dto.getBookingDate();
        this.checkIn = dto.getCheckIn();
        this.checkOut = dto.getCheckOut();
        this.hotelBookingNumber = dto.getHotelBookingNumber();
        this.fullName = dto.getFullName();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.invoiceAmount = dto.getInvoiceAmount();
        this.dueAmount = dto.getDueAmount();
        this.roomNumber = dto.getRoomNumber();
        this.couponNumber = dto.getCouponNumber();
        this.adults = dto.getAdults();
        this.children = dto.getChildren();
        this.rateAdult = dto.getRateAdult();
        this.rateChild = dto.getRateChild();
        this.hotelInvoiceNumber = dto.getHotelInvoiceNumber();
        this.folioNumber = dto.getFolioNumber();
        this.hotelAmount = dto.getHotelAmount();
        this.description = dto.getDescription();
        this.invoice = dto.getInvoice();
        this.ratePlan = dto.getRatePlan();
        this.nightType = dto.getNightType();
        this.roomType = dto.getRoomType();
        this.roomCategory = dto.getRoomCategory();
        this.roomRates = new ArrayList<>();
        this.nights = dto.getNights();
    }
}
