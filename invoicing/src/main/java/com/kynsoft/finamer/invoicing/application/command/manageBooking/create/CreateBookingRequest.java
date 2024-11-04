package com.kynsoft.finamer.invoicing.application.command.manageBooking.create;

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
public class CreateBookingRequest {

    private UUID id;
    private LocalDateTime hotelCreationDate;
    private LocalDateTime bookingDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String hotelBookingNumber;
    private String fullName;
    private String firstName;
    private String lastName;
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
    private String invoice;
    private String ratePlan;
    private String nightType;
    private String roomType;
    private String roomCategory;
    private String contract;
}
