package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingChildren;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import org.springframework.stereotype.Component;

@Component
public class UpdateBookingCalculateBookingChildrenCommandHandler implements ICommandHandler<UpdateBookingCalculateBookingChildrenCommand> {

    private final IManageBookingService bookingService;

    public UpdateBookingCalculateBookingChildrenCommandHandler(IManageBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public void handle(UpdateBookingCalculateBookingChildrenCommand command) {
        ManageBookingDto bookingDto = this.bookingService.findById(command.getBookingId());

        Double total = bookingDto.getRoomRates().stream()
                .mapToDouble(ManageRoomRateDto::getChildren)
                .sum();
        bookingDto.setChildren(total.intValue());
        this.bookingService.update(bookingDto);
    }

}
