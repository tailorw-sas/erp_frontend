package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateChickInAndCheckOut;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class UpdateBookingCalculateCheckInAndCheckOutCommandHandler implements ICommandHandler<UpdateBookingCalculateCheckIntAndCheckOutCommand> {

    private final IManageBookingService bookingService;

    public UpdateBookingCalculateCheckInAndCheckOutCommandHandler(IManageBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public void handle(UpdateBookingCalculateCheckIntAndCheckOutCommand command) {
        ManageBookingDto bookingDto = this.bookingService.findById(command.getBookingId());

        LocalDateTime checkIn = bookingDto.getRoomRates().stream()
                .map(ManageRoomRateDto::getCheckIn)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new IllegalStateException("No se encontró una fecha de entrada válida"));

        LocalDateTime checkOut = bookingDto.getRoomRates().stream()
                .map(ManageRoomRateDto::getCheckOut)
                .max(LocalDateTime::compareTo)
                .orElseThrow(() -> new IllegalStateException("No se encontró una fecha de salida válida"));
        bookingDto.setCheckIn(checkIn);
        bookingDto.setCheckOut(checkOut);
        this.bookingService.update(bookingDto);
    }

}
