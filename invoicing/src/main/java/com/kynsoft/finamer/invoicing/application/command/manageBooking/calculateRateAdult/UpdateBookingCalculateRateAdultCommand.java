package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateRateAdult;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookingCalculateRateAdultCommand implements ICommand {

    private ManageBookingDto bookingDto;

    public UpdateBookingCalculateRateAdultCommand(ManageBookingDto bookingDto) {
        this.bookingDto = bookingDto;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateBookingCalculateRateAdultMessage(bookingDto.getId());
    }
}
