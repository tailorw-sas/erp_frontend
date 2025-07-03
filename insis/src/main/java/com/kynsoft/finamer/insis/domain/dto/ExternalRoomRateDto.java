package com.kynsoft.finamer.insis.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalRoomRateDto {

    private String reservationCode;
    private String couponNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int stayDays;
    private String hotelCode;
    private String agencyCode;
    private String guestName;
    private String firstName;
    private String lastName;
    private int totalNumberOfGuest;
    private int adults;
    private int children;
    private Double amount;
    private String roomTypeCode;
    private String ratePlanCode;
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
    private String roomCategory;
    private String hash;
}
