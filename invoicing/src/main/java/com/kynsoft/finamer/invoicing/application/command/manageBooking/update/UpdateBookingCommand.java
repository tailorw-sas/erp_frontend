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
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    private Integer hotelBookingNumber;
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

    private UUID invoice;
    private UUID ratePlan;
    private UUID nightType;
    private UUID roomType;
    private UUID roomCategory;

    public UpdateBookingCommand(UUID id, LocalDateTime hotelCreationDate, LocalDateTime bookingDate, LocalDateTime checkIn, LocalDateTime checkOut, Integer hotelBookingNumber, String firstName, String lastName, Double invoiceAmount, String roomNumber, String couponNumber, Integer adults, Integer children, Double rateAdult, Double rateChild, String hotelInvoiceNumber, String folioNumber, Double hotelAmount, String description, UUID invoice, UUID ratePlan, UUID nightType, UUID roomType, UUID roomCategory) {
        this.id = id;
        this.hotelCreationDate = hotelCreationDate;
        this.bookingDate = bookingDate;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.hotelBookingNumber = hotelBookingNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.invoiceAmount = invoiceAmount;
        this.roomNumber = roomNumber;
        this.couponNumber = couponNumber;
        this.adults = adults;
        this.children = children;
        this.rateAdult = rateAdult;
        this.rateChild = rateChild;
        this.hotelInvoiceNumber = hotelInvoiceNumber;
        this.folioNumber = folioNumber;
        this.hotelAmount = hotelAmount;
        this.description = description;
        this.invoice = invoice;
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
                request.getCheckIn(),
                request.getCheckOut(),
                request.getHotelBookingNumber(),
                request.getFirstName(),
                request.getLastName(),
                request.getInvoiceAmount(),
                request.getRoomNumber(),
                request.getCouponNumber(),
                request.getAdults(),
                request.getChildren(),
                request.getRateAdult(),
                request.getRateChild(),
                request.getHotelInvoiceNumber(),
                request.getFolioNumber(),
                request.getHotelAmount(),
                request.getDescription(),
                request.getInvoice(),
                request.getRatePlan(),
                request.getNightType(),
                request.getRoomType(),
                request.getRoomCategory()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateBookingMessage(id);
    }
}
