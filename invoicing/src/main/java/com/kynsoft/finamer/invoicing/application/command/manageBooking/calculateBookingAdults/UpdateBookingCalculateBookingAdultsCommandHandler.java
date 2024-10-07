package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingAdults;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import org.springframework.stereotype.Component;

@Component
public class UpdateBookingCalculateBookingAdultsCommandHandler implements ICommandHandler<UpdateBookingCalculateBookingAdultsCommand> {

    public UpdateBookingCalculateBookingAdultsCommandHandler() {
    }

    @Override
    public void handle(UpdateBookingCalculateBookingAdultsCommand command) {

        Double total = command.getBookingDto().getRoomRates().stream()
                .mapToDouble(ManageRoomRateDto::getAdults)
                .sum();
        command.getBookingDto().setAdults(total.intValue());
    }

}
