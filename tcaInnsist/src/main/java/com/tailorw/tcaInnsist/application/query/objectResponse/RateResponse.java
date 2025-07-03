package com.tailorw.tcaInnsist.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsof.share.utils.DateConvert;
import com.tailorw.tcaInnsist.domain.dto.RateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
public class RateResponse implements IResponse {

    private String reservationCode;
    private String couponNumber;
    private String checkInDate;
    private String checkOutDate;
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
    private String invoicingDate;
    private String hotelCreationDate;
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

    public RateResponse(RateDto dto){
        this.reservationCode = dto.getReservationCode();
        this.couponNumber = dto.getCouponNumber();
        this.checkInDate = DateConvert.convertLocalDateToString(dto.getCheckInDate(), DateConvert.getIsoLocalDateFormatter());
        this.checkOutDate = DateConvert.convertLocalDateToString(dto.getCheckOutDate(), DateConvert.getIsoLocalDateFormatter());
        this.stayDays = dto.getStayDays();
        this.hotelCode = dto.getHotelCode();
        this.agencyCode = dto.getAgencyCode();
        this.guestName = dto.getGuestName();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.totalNumberOfGuest = dto.getTotalNumberOfGuest();
        this.adults = dto.getAdults();
        this.children = dto.getChildren();
        this.amount = dto.getAmount();
        this.roomTypeCode = dto.getRoomTypeCode();
        this.ratePlanCode = dto.getRatePlanCode();
        this.invoicingDate = DateConvert.convertLocalDateToString(dto.getInvoicingDate(), DateConvert.getIsoLocalDateFormatter());
        this.hotelCreationDate = DateConvert.convertLocalDateToString(dto.getHotelCreationDate(), DateConvert.getIsoLocalDateFormatter());
        this.originalAmount = dto.getOriginalAmount();
        this.amountPaymentApplied = this.getAmountPaymentApplied();
        this.rateByAdult = dto.getRateByAdult();
        this.rateByChild = dto.getRateByChild();
        this.remarks = dto.getRemarks();
        this.roomNumber = dto.getRoomNumber();
        this.hotelInvoiceAmount = dto.getHotelInvoiceAmount();
        this.hotelInvoiceNumber = dto.getHotelInvoiceNumber();
        this.invoiceFolioNumber = dto.getInvoiceFolioNumber();
        this.quote = dto.getQuote();
        this.renewalNumber = dto.getRenewalNumber();
        this.roomCategory = dto.getRoomCategory();
        this.hash = dto.getHash();
    }
}
