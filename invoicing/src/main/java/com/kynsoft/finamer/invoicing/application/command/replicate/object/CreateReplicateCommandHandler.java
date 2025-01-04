package com.kynsoft.finamer.invoicing.application.command.replicate.object;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageBooking.ProducerReplicateManageBookingService;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CreateReplicateCommandHandler implements ICommandHandler<CreateReplicateCommand> {

    private final IManageInvoiceService manageInvoiceService;
    private final IManageBookingService manageBookingService;
    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;
    private final ProducerReplicateManageBookingService producerReplicateManageBookingService;

    public CreateReplicateCommandHandler(IManageInvoiceService manageInvoiceService,
            IManageBookingService manageBookingService,
            ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService,
            ProducerReplicateManageBookingService producerReplicateManageBookingService) {
        this.manageInvoiceService = manageInvoiceService;
        this.manageBookingService = manageBookingService;
        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
        this.producerReplicateManageBookingService = producerReplicateManageBookingService;
    }

    @Override
    public void handle(CreateReplicateCommand command) {
        for (ObjectEnum object : command.getObjects()) {
            switch (object) {
                case MANAGE_INVOICE -> {
                    List<ManageInvoiceDto> list = this.manageInvoiceService.findAllToReplicate();
                    for (ManageInvoiceDto invoice : list) {
                        this.producerReplicateManageInvoiceService.create(invoice, null);
                    }
                }
                case MANAGE_BOOKING -> {
                    List<ManageBookingDto> list = this.manageBookingService.findAllToReplicate();
                    for (ManageBookingDto booking : list) {
                        this.producerReplicateManageBookingService.create(booking);
                    }
                }
                default ->
                    System.out.println("Número inválido. Por favor, intenta de nuevo con un número del 1 al 7.");
            }
        }
    }
}
