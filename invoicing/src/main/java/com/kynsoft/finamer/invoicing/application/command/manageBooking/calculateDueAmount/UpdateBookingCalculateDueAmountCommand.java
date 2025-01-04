package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateDueAmount;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookingCalculateDueAmountCommand implements ICommand {

    private ManageBookingDto bookingDto;
    private Double rateAmount;

    public UpdateBookingCalculateDueAmountCommand(ManageBookingDto bookingDto, Double rateAmount) {
        this.bookingDto = bookingDto;
        this.rateAmount = rateAmount;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateBookingCalculateDueAmountMessage(bookingDto.getId());
    }
}
