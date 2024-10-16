package com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.other;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateRoomRateOtherCommand implements ICommand {

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
    private IMediator mediator;

    public CreateRoomRateOtherCommand(LocalDateTime checkIn, LocalDateTime checkOut, Double invoiceAmount, String roomNumber,
            Integer adults, Integer children, Double rateAdult, Double rateChild, Double hotelAmount, String remark,
            UUID booking, UUID id, IMediator mediator) {
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
        this.mediator = mediator;
    }

    public static CreateRoomRateOtherCommand fromRequest(CreateRoomRateOtherRequest request, IMediator mediator) {
        return new CreateRoomRateOtherCommand(
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
                request.getId() != null ? request.getId() : UUID.randomUUID(),
                mediator
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateRoomRateOtherMessage(id);
    }
}
