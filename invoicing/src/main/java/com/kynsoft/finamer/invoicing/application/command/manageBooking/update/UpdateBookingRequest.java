package com.kynsoft.finamer.invoicing.application.command.manageBooking.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingRequest {

    private UUID id;
    private LocalDateTime hotelCreationDate;
    private LocalDateTime bookingDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    private String hotelBookingNumber;
    private String fullName;
    private Double invoiceAmount;
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

    private UUID invoice;
    private UUID ratePlan;
    private UUID nightType;
    private UUID roomType;
    private UUID roomCategory;
}
