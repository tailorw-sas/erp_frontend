package com.kynsoft.finamer.invoicing.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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
}
