package com.kynsoft.finamer.insis.application.command.roomRate.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import com.kynsoft.finamer.insis.domain.dto.BookingDto;
import com.kynsoft.finamer.insis.infrastructure.model.enums.RoomRateStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateRoomRateCommand implements ICommand {

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
    private int childrens;
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

    public CreateRoomRateCommand(String hotel,
                                 String agency,
                                 LocalDate checkInDate,
                                 LocalDate checkOutDate,
                                 int stayDays,
                                 String reservationCode,
                                 String guestName,
                                 String firstName,
                                 String lastName,
                                 Double amount,
                                 String roomType,
                                 String couponNumber,
                                 int totalNumberOfGuest,
                                 int adults,
                                 int childrens,
                                 String ratePlan,
                                 LocalDate invoicingDate,
                                 LocalDate hotelCreationDate,
                                 Double originalAmount,
                                 Double amountPaymentApplied,
                                 Double rateByAdult,
                                 Double rateByChild,
                                 String remarks,
                                 String roomNumber,
                                 Double hotelInvoiceAmount,
                                 String hotelInvoiceNumber,
                                 String invoiceFolioNumber,
                                 Double quote,
                                 String renewalNumber,
                                 String roomCategory,
                                 String hash)
    {
        this.id = UUID.randomUUID();
        this.hotel = hotel;
        this.agency = agency;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.stayDays = stayDays;
        this.reservationCode = reservationCode;
        this.guestName = guestName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.amount = amount;
        this.roomType = roomType;
        this.couponNumber = couponNumber;
        this.totalNumberOfGuest = totalNumberOfGuest;
        this.adults = adults;
        this.childrens = childrens;
        this.ratePlan = ratePlan;
        this.invoicingDate = invoicingDate;
        this.hotelCreationDate = hotelCreationDate;
        this.originalAmount = originalAmount;
        this.amountPaymentApplied = amountPaymentApplied;
        this.rateByAdult = rateByAdult;
        this.rateByChild = rateByChild;
        this.remarks = remarks;
        this.roomNumber = roomNumber;
        this.hotelInvoiceAmount = hotelInvoiceAmount;
        this.hotelInvoiceNumber = hotelInvoiceNumber;
        this.invoiceFolioNumber = invoiceFolioNumber;
        this.quote = quote;
        this.renewalNumber = renewalNumber;
        this.roomCategory = roomCategory;
        this.hash = hash;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateRoomRateMessage(id);
    }
}
