package com.kynsoft.finamer.insis.application.services.roomRate.create;

import com.kynsoft.finamer.insis.application.command.roomRate.create.CreateRoomRateCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateRoomRateRequest {

    private UUID id;
    private String hotel;
    private LocalDateTime updatedAt;
    private String agency;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int stayDays;
    private String reservationCode;
    private String guestName;
    private String firstName;
    private String lastName;
    private Double amount;
    private String roomType;
    private String couponNumber;
    private int totalNumberOfGuest;
    private int adults;
    private int children;
    private String ratePlan;
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

    public CreateRoomRateRequest(CreateRoomRateCommand command){
        this.id = command.getId();
        this.hotel = command.getHotel();
        this.updatedAt = command.getUpdatedAt();
        this.agency = command.getAgency();
        this.checkInDate = command.getCheckInDate();
        this.checkOutDate = command.getCheckOutDate();
        this.stayDays = command.getStayDays();
        this.reservationCode = command.getReservationCode();
        this.guestName = command.getGuestName();
        this.firstName = command.getFirstName();
        this.lastName = command.getLastName();
        this.amount = command.getAmount();
        this.roomType = command.getRoomType();
        this.couponNumber = command.getCouponNumber();
        this.totalNumberOfGuest = command.getTotalNumberOfGuest();
        this.adults = command.getAdults();
        this.children = command.getChildrens();
        this.ratePlan = command.getRatePlan();
        this.invoicingDate = command.getInvoicingDate();
        this.hotelCreationDate = command.getHotelCreationDate();
        this.originalAmount = command.getOriginalAmount();
        this.amountPaymentApplied = command.getAmountPaymentApplied();
        this.rateByAdult = command.getRateByAdult();
        this.rateByChild = command.getRateByChild();
        this.remarks = command.getRemarks();
        this.roomNumber = command.getRoomNumber();
        this.hotelInvoiceAmount = command.getHotelInvoiceAmount();
        this.hotelInvoiceNumber = command.getHotelInvoiceNumber();
        this.invoiceFolioNumber = command.getInvoiceFolioNumber();
        this.quote = command.getQuote();
        this.renewalNumber = command.getRenewalNumber();
        this.roomCategory = command.getRoomCategory();
        this.hash = command.getHash();
    }
}
