package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateHotelAmount;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import org.springframework.stereotype.Component;

@Component
public class UpdateBookingCalculateHotelAmountCommandHandler implements ICommandHandler<UpdateBookingCalculateHotelAmountCommand> {

    public UpdateBookingCalculateHotelAmountCommandHandler() {
    }

    @Override
    public void handle(UpdateBookingCalculateHotelAmountCommand command) {

        double total = command.getBookingDto().getRoomRates().stream()
                .mapToDouble(ManageRoomRateDto::getHotelAmount)
                .sum();
        command.getBookingDto().setHotelAmount(total);
    }

}
