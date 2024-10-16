package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateRateChild;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UpdateBookingCalculateRateChildCommandHandler implements ICommandHandler<UpdateBookingCalculateRateChildCommand> {

    public UpdateBookingCalculateRateChildCommandHandler() {
    }

    @Override
    public void handle(UpdateBookingCalculateRateChildCommand command) {

        double total = command.getBookingDto().getRoomRates().stream()
                .mapToDouble(rate -> Optional.ofNullable(rate.getRateChild())
                .orElse(0.0))
                .sum();
        command.getBookingDto().setRateChild(total);
    }

}
