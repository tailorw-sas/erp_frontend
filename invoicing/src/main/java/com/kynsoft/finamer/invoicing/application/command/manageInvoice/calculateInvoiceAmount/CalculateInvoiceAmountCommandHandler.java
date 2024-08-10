package com.kynsoft.finamer.invoicing.application.command.manageInvoice.calculateInvoiceAmount;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import org.springframework.stereotype.Component;

@Component
public class CalculateInvoiceAmountCommandHandler implements ICommandHandler<CalculateInvoiceAmountCommand> {

    private final IManageInvoiceService service;
    private final IManageBookingService bookingService;
    private final IManageRoomRateService rateService;


    public CalculateInvoiceAmountCommandHandler(IManageInvoiceService service, IManageBookingService bookingService,
                                                IManageRoomRateService rateService) {
        this.service = service;
        this.bookingService = bookingService;
        this.rateService = rateService;
    }


    @Override
    public void handle(CalculateInvoiceAmountCommand command) {


        try {
            for (int i = 0; i < command.getBookings().size(); i++) {
                this.bookingService.calculateInvoiceAmount(this.bookingService.findById(command.getBookings().get(i)));
            }

            this.service.calculateInvoiceAmount(this.service.findById(command.getId()));
        } catch (Exception ignored) {

        }


    }

}
