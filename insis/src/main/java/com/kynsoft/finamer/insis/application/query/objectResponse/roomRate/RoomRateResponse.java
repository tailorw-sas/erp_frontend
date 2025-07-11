package com.kynsoft.finamer.insis.application.query.objectResponse.roomRate;

import com.kynsoft.finamer.insis.application.query.objectResponse.manageAgency.ManageAgencyResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageHotel.ManageHotelResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageRatePlan.ManageRatePlanResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageRoomCategory.ManageRoomCategoryResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageRoomType.ManageRoomTypeResponse;
import com.kynsoft.finamer.insis.domain.dto.RoomRateDto;
import com.kynsoft.finamer.insis.infrastructure.model.RoomRate;
import com.kynsoft.finamer.insis.infrastructure.model.enums.RoomRateStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class RoomRateResponse {

    private UUID id;
    private RoomRateStatus status;
    private ManageHotelResponse hotel;
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
    private ManageRoomCategoryResponse roomCategory;
    private String message;
    private Boolean hasErrors;

    public RoomRateResponse(RoomRateDto dto){
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.hotel = Objects.nonNull(dto.getHotel()) ? new ManageHotelResponse(dto.getHotel()) : null;
        this.agency = Objects.nonNull(dto.getAgency()) ? new ManageAgencyResponse(dto.getAgency()) : null;
        this.checkInDate= dto.getCheckInDate();
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
        this.childrens = dto.getChildren();
        this.ratePlan = Objects.nonNull(dto.getRatePlan()) ? new ManageRatePlanResponse(dto.getRatePlan()) : null;
        this.invoicingDate = dto.getInvoicingDate();
        this.hotelCreationDate = dto.getHotelCreationDate();
        this.originalAmount = dto.getOriginalAmount();
        this.amountPaymentApplied = dto.getAmountPaymentApplied();
        this.rateByAdult = dto.getRateByAdult();
        this.rateByChild = dto.getRateByChild();
        this.remarks = dto.getRemarks();
        this.roomNumber =dto.getRoomNumber();
        this.hotelInvoiceAmount = dto.getHotelInvoiceAmount();
        this.hotelInvoiceNumber = dto.getHotelInvoiceNumber();
        this.invoiceFolioNumber = dto.getInvoiceFolioNumber();
        this.quote = dto.getQuote();
        this.renewalNumber = dto.getRenewalNumber();
        this.roomCategory = Objects.nonNull(dto.getRoomCategory()) ? new ManageRoomCategoryResponse(dto.getRoomCategory()) : null;
        this.message = dto.getMessage();
        this.setHasErrors(hasErrors(dto));
    }

    private boolean hasErrors(RoomRateDto roomRateDto){
        if(Objects.isNull(roomRateDto.getAgency())){
            return true;
        }

        return false;
    }
}
