package com.kynsoft.finamer.insis.application.command.booking.undoImportBooking;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsoft.finamer.insis.domain.dto.BookingDto;
import com.kynsoft.finamer.insis.domain.services.IBookingService;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BookingStatus;
import org.springframework.stereotype.Component;

@Component
public class UndoImportBookingCommandHandler implements ICommandHandler<UndoImportBookingCommand> {

    private final IBookingService bookingService;

    public UndoImportBookingCommandHandler(IBookingService bookingService){
        this.bookingService = bookingService;
    }


    @Override
    public void handle(UndoImportBookingCommand command) {

        BookingDto booking = bookingService.findById(command.getBookingId());

        ConsumerUpdate update = new ConsumerUpdate();
        booking.setStatus(BookingStatus.PENDING);

        bookingService.update(booking);
    }
}
