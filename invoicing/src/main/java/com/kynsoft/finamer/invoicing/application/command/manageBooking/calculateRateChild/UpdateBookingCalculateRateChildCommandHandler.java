package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateRateChild;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import org.springframework.stereotype.Component;

@Component
public class UpdateBookingCalculateRateChildCommandHandler implements ICommandHandler<UpdateBookingCalculateRateChildCommand> {

    public UpdateBookingCalculateRateChildCommandHandler() {
    }

    @Override
    public void handle(UpdateBookingCalculateRateChildCommand command) {

        double total = command.getBookingDto().getRoomRates().stream()
                .mapToDouble(ManageRoomRateDto::getRateChild)
                .sum();
        command.getBookingDto().setRateChild(total);
    }

}
