package com.kynsoft.finamer.payment.application.command.invoice.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateInvoiceCommand implements ICommand {

    private BookingHttp bookingHttp;

    public CreateInvoiceCommand(BookingHttp bookingHttp) {
        this.bookingHttp = bookingHttp;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateInvoiceMessage();
    }
}
