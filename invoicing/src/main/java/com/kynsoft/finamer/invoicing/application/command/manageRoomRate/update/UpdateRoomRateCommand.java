package com.kynsoft.finamer.invoicing.application.command.manageRoomRate.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateRoomRateCommand implements ICommand {

    private UUID id;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Double invoiceAmount;
    private Integer adults;
    private Integer children;
    private Double hotelAmount;
    private IMediator mediator;

    public UpdateRoomRateCommand(UUID id, LocalDateTime checkIn, LocalDateTime checkOut, Double invoiceAmount, Integer adults, Integer children, Double hotelAmount, IMediator mediator) {
        this.id = id;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.invoiceAmount = invoiceAmount;
        this.adults = adults;
        this.children = children;
        this.hotelAmount = hotelAmount;
        this.mediator = mediator;
    }

    public static UpdateRoomRateCommand fromRequest(UpdateRoomRateRequest request, UUID id, IMediator mediator) {
        return new UpdateRoomRateCommand(
                id,
                request.getCheckIn(),
                request.getCheckOut(),
                request.getInvoiceAmount(),
                request.getAdults(),
                request.getChildren(),
                request.getHotelAmount(),
                mediator
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateRoomRateMessage(id);
    }
}
