package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingChildren;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import org.springframework.stereotype.Component;

@Component
public class UpdateBookingCalculateBookingChildrenCommandHandler implements ICommandHandler<UpdateBookingCalculateBookingChildrenCommand> {

    public UpdateBookingCalculateBookingChildrenCommandHandler() {
    }

    @Override
    public void handle(UpdateBookingCalculateBookingChildrenCommand command) {

        Double total = command.getBookingDto().getRoomRates().stream()
                .mapToDouble(ManageRoomRateDto::getChildren)
                .sum();
        command.getBookingDto().setChildren(total.intValue());
    }

}
