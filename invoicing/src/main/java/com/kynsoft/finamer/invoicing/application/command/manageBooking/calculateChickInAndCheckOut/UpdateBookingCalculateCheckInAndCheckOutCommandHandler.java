package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateChickInAndCheckOut;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class UpdateBookingCalculateCheckInAndCheckOutCommandHandler implements ICommandHandler<UpdateBookingCalculateCheckIntAndCheckOutCommand> {

    public UpdateBookingCalculateCheckInAndCheckOutCommandHandler() {
    }

    @Override
    public void handle(UpdateBookingCalculateCheckIntAndCheckOutCommand command) {

        LocalDateTime checkIn = command.getBookingDto().getRoomRates().stream()
                .map(ManageRoomRateDto::getCheckIn)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new IllegalStateException("No se encontró una fecha de entrada válida"));

        LocalDateTime checkOut = command.getBookingDto().getRoomRates().stream()
                .map(ManageRoomRateDto::getCheckOut)
                .max(LocalDateTime::compareTo)
                .orElseThrow(() -> new IllegalStateException("No se encontró una fecha de salida válida"));
        command.getBookingDto().setCheckIn(checkIn);
        command.getBookingDto().setCheckOut(checkOut);
    }

}
