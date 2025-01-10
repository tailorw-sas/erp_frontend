package com.tailorw.tcaInnsist.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.tailorw.tcaInnsist.domain.dto.RateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class RateResponse implements IResponse {

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
    private int childrens;
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
    private String hash;

    public RateResponse(RateDto dto){
        this.reservationCode = dto.getReservationCode();
        this.couponNumber = dto.getCouponNumber();
        this.checkInDate = dto.getCheckInDate();
        this.checkOutDate = dto.getCheckOutDate();
        this.stayDays = dto.getStayDays();
        this.hotelCode = dto.getHotelCode();
        this.agencyCode = dto.getAgencyCode();
        this.guestName = dto.getGuestName();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.totalNumberOfGuest = dto.getTotalNumberOfGuest();
        this.adults = dto.getAdults();
        this.childrens = dto.getChildrens();
        this.amount = dto.getAmount();
        this.roomTypeCode = dto.getRoomTypeCode();
        this.ratePlanCode = dto.getRatePlanCode();
        this.invoicingDate = dto.getInvoicingDate();
        this.hotelCreationDate = dto.getHotelCreationDate();
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
        this.hash = dto.getHash();
    }
}
