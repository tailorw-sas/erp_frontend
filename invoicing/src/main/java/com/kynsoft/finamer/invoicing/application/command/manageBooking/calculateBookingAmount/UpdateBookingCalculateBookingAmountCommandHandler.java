package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingAmount;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import org.springframework.stereotype.Component;

@Component
public class UpdateBookingCalculateBookingAmountCommandHandler implements ICommandHandler<UpdateBookingCalculateBookingAmountCommand> {

    private final IManageBookingService bookingService;

    public UpdateBookingCalculateBookingAmountCommandHandler(IManageBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public void handle(UpdateBookingCalculateBookingAmountCommand command) {
        ManageBookingDto bookingDto = this.bookingService.findById(command.getBookingId());

        double total = bookingDto.getRoomRates().stream()
                .mapToDouble(ManageRoomRateDto::getInvoiceAmount)
                .sum();
        bookingDto.setInvoiceAmount(total);
        this.bookingService.update(bookingDto);
    }

}
