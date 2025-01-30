package com.kynsoft.finamer.insis.application.query.objectResponse.booking;

import com.kynsoft.finamer.insis.application.query.objectResponse.manageAgency.ManageAgencyResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageHotel.ManageHotelResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageRatePlan.ManageRatePlanResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageRoomType.ManageRoomTypeResponse;
import com.kynsoft.finamer.insis.domain.dto.BookingDto;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;
import java.util.Objects;

@Getter
@Setter
public class BookingResponse {

    private UUID id;
    private BookingStatus status;
    private ManageHotelResponse hotel;
    private String agencyCode;
    private ManageAgencyResponse agency;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int stayDays;
    private String reservationCode;
    private String guestName;
    private String firstName;
    private String lastName;
    private Double amount;
    private ManageRoomTypeResponse roomType;
    private String couponNumber;
    private int totalNumberOfGuest;
    private int adults;
    private int childrens;
    private ManageRatePlanResponse ratePlan;
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
    private String message;

    public BookingResponse(BookingDto dto){
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.hotel = Objects.nonNull(dto.getHotel()) ? new ManageHotelResponse(dto.getHotel()) : null;
        this.agencyCode = dto.getAgencyCode();
        this.agency = Objects.nonNull(dto.getAgency()) ? new ManageAgencyResponse(dto.getAgency()) : null;
        this.checkInDate = dto.getCheckInDate();
        this.checkOutDate = dto.getCheckOutDate();
        this.stayDays = dto.getStayDays();
        this.reservationCode = dto.getReservationCode();
        this.guestName = dto.getGuestName();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.amount = dto.getAmount();
        this.roomType = Objects.nonNull(dto.getRoomType()) ? new ManageRoomTypeResponse(dto.getRoomType()) : null;
        this.couponNumber = dto.getCouponNumber();
        this.totalNumberOfGuest = dto.getTotalNumberOfGuest();
        this.adults = dto.getAdults();
        this.childrens = dto.getChildrens();
        this.ratePlan = Objects.nonNull(dto.getRatePlan()) ? new ManageRatePlanResponse(dto.getRatePlan()) : null;
        this.invoicingDate = dto.getInvoicingDate();
        this.hotelCreationDate = dto.getHotelCreationDate();
        this.originalAmount = dto.getOriginalAmount();
        this.amountPaymentApplied = dto.getAmountPaymentApplied();
        this.rateByAdult = dto.getRateByAdult();
        this.rateByChild = dto.getRateByChild();
        this.remarks = dto.getRemarks();
        this.roomNumber = dto.getRoomNumber();
        this.hotelInvoiceAmount = dto.getHotelInvoiceAmount();
        this.invoiceFolioNumber = dto.getInvoiceFolioNumber();
        this.quote = dto.getQuote();
        this.renewalNumber = dto.getRenewalNumber();
        this.message = dto.getMessage();
    }
}
