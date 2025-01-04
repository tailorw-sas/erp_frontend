package com.kynsoft.finamer.invoicing.application.command.manageBooking.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateBookingCommand implements ICommand {

    private UUID id;
    private LocalDateTime hotelCreationDate;
    private LocalDateTime bookingDate;
    //private LocalDateTime checkIn;
    //private LocalDateTime checkOut;

    private String hotelBookingNumber;
    private String fullName;
    private String firstName;
    private String lastName;
    //private Double invoiceAmount;
    private String roomNumber;
    private String couponNumber;
    //private Integer adults;
    //private Integer children;
    //private Double rateAdult;
    //private Double rateChild;
    private String hotelInvoiceNumber;
    private String folioNumber;
    //private Double hotelAmount;
    private String description;
    private String contract;

    //private UUID invoice;
    private UUID ratePlan;
    private UUID nightType;
    private UUID roomType;
    private UUID roomCategory;

    public UpdateBookingCommand(UUID id, LocalDateTime hotelCreationDate, LocalDateTime bookingDate,
            String hotelBookingNumber, String fullName, String firstName, String lastName, String roomNumber,
            String couponNumber, String hotelInvoiceNumber, String folioNumber, String description, String contract,
            UUID ratePlan, UUID nightType, UUID roomType, UUID roomCategory) {
        this.id = id;
        this.hotelCreationDate = hotelCreationDate;
        this.bookingDate = bookingDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hotelBookingNumber = hotelBookingNumber;
        this.fullName = fullName;
        this.roomNumber = roomNumber;
        this.couponNumber = couponNumber;
        this.hotelInvoiceNumber = hotelInvoiceNumber;
        this.folioNumber = folioNumber;
        this.description = description;
        this.contract = contract;
        this.ratePlan = ratePlan;
        this.nightType = nightType;
        this.roomType = roomType;
        this.roomCategory = roomCategory;
    }

    public static UpdateBookingCommand fromRequest(UpdateBookingRequest request, UUID id) {
        return new UpdateBookingCommand(
                id,
                request.getHotelCreationDate(),
                request.getBookingDate(),
                request.getHotelBookingNumber(),
                request.getFullName(),
                request.getFirstName(),
                request.getLastName(),
                request.getRoomNumber(),
                request.getCouponNumber(),
                request.getHotelInvoiceNumber(),
                request.getFolioNumber(),
                request.getDescription(),
                request.getContract(),
                request.getRatePlan(),
                request.getNightType(),
                request.getRoomType(),
                request.getRoomCategory());
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateBookingMessage(id);
    }
}
