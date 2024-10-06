package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateRateAdult;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import org.springframework.stereotype.Component;

@Component
public class UpdateBookingCalculateRateAdultCommandHandler implements ICommandHandler<UpdateBookingCalculateRateAdultCommand> {

    public UpdateBookingCalculateRateAdultCommandHandler() {
    }

    @Override
    public void handle(UpdateBookingCalculateRateAdultCommand command) {

        double total = command.getBookingDto().getRoomRates().stream()
                .mapToDouble(ManageRoomRateDto::getRateAdult)
                .sum();
        command.getBookingDto().setRateAdult(total);
    }

}
