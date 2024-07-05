package com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateRoomRateCommand implements ICommand {

    private UUID id;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Double invoiceAmount;
    private String roomNumber;
    private Integer adults;
    private Integer children;
    private Double rateAdult;
    private Double rateChild;
    private Double hotelAmount;
    private String remark;
    private UUID booking;

    public CreateRoomRateCommand(LocalDateTime checkIn, LocalDateTime checkOut, Double invoiceAmount, String roomNumber,
            Integer adults, Integer children, Double rateAdult, Double rateChild, Double hotelAmount, String remark,
            UUID booking, UUID id) {
        this.id = id;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.invoiceAmount = invoiceAmount;
        this.roomNumber = roomNumber;
        this.adults = adults;
        this.children = children;
        this.rateAdult = rateAdult;
        this.rateChild = rateChild;
        this.hotelAmount = hotelAmount;
        this.remark = remark;
        this.booking = booking;
    }

    public static CreateRoomRateCommand fromRequest(CreateRoomRateRequest request) {
        return new CreateRoomRateCommand(
                request.getCheckIn(),
                request.getCheckOut(),
                request.getInvoiceAmount(),
                request.getRoomNumber(),
                request.getAdults(),
                request.getChildren(),
                request.getRateAdult(),
                request.getRateChild(),
                request.getHotelAmount(),
                request.getRemark(),
                request.getBooking(),
                request.getId() != null ? request.getId() : UUID.randomUUID());
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateRoomRateMessage(id);
    }
}
