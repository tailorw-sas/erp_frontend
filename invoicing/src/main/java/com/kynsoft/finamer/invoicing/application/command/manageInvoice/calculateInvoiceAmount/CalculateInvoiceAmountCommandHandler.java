package com.kynsoft.finamer.invoicing.application.command.manageInvoice.calculateInvoiceAmount;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Component;

@Component
public class CalculateInvoiceAmountCommandHandler implements ICommandHandler<CalculateInvoiceAmountCommand> {

    private final IManageInvoiceService service;
    private final IManageBookingService bookingService;
    private final IManageRoomRateService rateService;
    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;

    public CalculateInvoiceAmountCommandHandler(IManageInvoiceService service,
                                                IManageBookingService bookingService,
                                                IManageRoomRateService rateService,
                                                ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService) {
        this.service = service;
        this.bookingService = bookingService;
        this.rateService = rateService;
        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
    }

    @Override
    public void handle(CalculateInvoiceAmountCommand command) {

        try {
            for (int i = 0; i < command.getBookings().size(); i++) {
                this.bookingService.calculateInvoiceAmount(this.bookingService.findById(command.getBookings().get(i)));
            }

            this.service.calculateInvoiceAmount(this.service.findById(command.getId()));
            this.producerReplicateManageInvoiceService.create(this.service.findById(command.getId()));
        } catch (Exception ignored) {
            System.out.println(ignored);
        }

    }

}
