package com.kynsoft.finamer.invoicing.application.command.manageRoomRate.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
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

    public UpdateRoomRateCommand(UUID id, LocalDateTime checkIn, LocalDateTime checkOut, Double invoiceAmount, Integer adults, Integer children, Double hotelAmount) {
        this.id = id;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.invoiceAmount = invoiceAmount;
        this.adults = adults;
        this.children = children;
        this.hotelAmount = hotelAmount;
    }

    public static UpdateRoomRateCommand fromRequest(UpdateRoomRateRequest request, UUID id) {
        return new UpdateRoomRateCommand(
                id,
                request.getCheckIn(),
                request.getCheckOut(),
                request.getInvoiceAmount(),
                request.getAdults(),
                request.getChildren(),
                request.getHotelAmount()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateRoomRateMessage(id);
    }
}
