package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateRateAdult;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UpdateBookingCalculateRateAdultCommandHandler implements ICommandHandler<UpdateBookingCalculateRateAdultCommand> {

    public UpdateBookingCalculateRateAdultCommandHandler() {
    }

    @Override
    public void handle(UpdateBookingCalculateRateAdultCommand command) {

        double total = command.getBookingDto().getRoomRates().stream()
                .mapToDouble(rate -> Optional.ofNullable(rate.getRateAdult())
                .orElse(0.0))
                .sum();
        command.getBookingDto().setRateAdult(total);
    }

}
