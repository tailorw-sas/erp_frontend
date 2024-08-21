package com.kynsoft.finamer.payment.application.command.replicate.delete.objects;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;

import org.springframework.stereotype.Component;

@Component
public class DeleteReplicateCommandHandler implements ICommandHandler<DeleteReplicateCommand> {

    private final IManageInvoiceService invoiceService;
    private final IManageBookingService bookingService;

    public DeleteReplicateCommandHandler(IManageInvoiceService invoiceService,
            IManageBookingService bookingService) {
        this.invoiceService = invoiceService;
        this.bookingService = bookingService;
    }

    @Override
    public void handle(DeleteReplicateCommand command) {
        for (DeleteObjectEnum object : command.getObjects()) {
            switch (object) {
                case DELETE_MANAGE_INVOICE -> {
                    this.invoiceService.deleteAll();
                }
                case DELETE_MANAGE_BOOKING -> {
                    this.bookingService.deleteAll();
                }
                default ->
                    System.out.println("Número inválido. Por favor, intenta de nuevo con un número del 1 al 7.");
            }
        }
    }
}
