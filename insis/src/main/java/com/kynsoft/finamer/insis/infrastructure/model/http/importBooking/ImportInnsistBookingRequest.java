package com.kynsoft.finamer.insis.infrastructure.model.http.importBooking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportInnsistBookingRequest implements Serializable {
    private UUID id;
    private LocalDateTime hotelCreationDate;
    private LocalDateTime invoiceDate;
    private LocalDateTime bookingDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String hotelBookingNumber;
    private String firstName;
    private String lastName;
    private String roomNumber;
    private Integer adults;
    private Integer children;
    private Integer nights;
    private Double rateAdult;
    private Double rateChild;
    private Long hotelInvoiceNumber;
    private String folioNumber;
    private Double hotelAmount;
    private String description;
    private String ratePlanCode;
    private String nightTypeCode;
    private String roomTypeCode;
    private String roomCategoryCode;
    private String manageHotelCode;
    private String manageAgencyCode;
    private String couponNumber;
    private List<ImportInnsistRoomRateRequest> roomRates;
    private String generationType;
    private boolean virtualHotel;
}
