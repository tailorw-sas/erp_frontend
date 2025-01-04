package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingAdults;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookingCalculateBookingAdultsCommand implements ICommand {

    private ManageBookingDto bookingDto;

    public UpdateBookingCalculateBookingAdultsCommand(ManageBookingDto bookingDto) {
        this.bookingDto = bookingDto;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateBookingCalculateBookingAdultsMessage(bookingDto.getId());
    }
}
