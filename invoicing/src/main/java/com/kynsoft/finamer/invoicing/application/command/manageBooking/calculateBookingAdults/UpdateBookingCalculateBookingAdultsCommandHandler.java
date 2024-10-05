package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingAdults;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import org.springframework.stereotype.Component;

@Component
public class UpdateBookingCalculateBookingAdultsCommandHandler implements ICommandHandler<UpdateBookingCalculateBookingAdultsCommand> {

    private final IManageBookingService bookingService;

    public UpdateBookingCalculateBookingAdultsCommandHandler(IManageBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public void handle(UpdateBookingCalculateBookingAdultsCommand command) {
        ManageBookingDto bookingDto = this.bookingService.findById(command.getBookingId());

        Double total = bookingDto.getRoomRates().stream()
                .mapToDouble(ManageRoomRateDto::getAdults)
                .sum();
        bookingDto.setAdults(total.intValue());
        this.bookingService.update(bookingDto);
    }

}
