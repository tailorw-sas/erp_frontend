package com.kynsoft.finamer.invoicing.application.command.replicate.object;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Component;

@Component
public class CreateReplicateCommandHandler implements ICommandHandler<CreateReplicateCommand> {

    private final IManageInvoiceService manageInvoiceService;
    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;

    public CreateReplicateCommandHandler(IManageInvoiceService manageInvoiceService,
                                         ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService) {
        this.manageInvoiceService = manageInvoiceService;
        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
    }

    @Override
    public void handle(CreateReplicateCommand command) {
        for (ObjectEnum object : command.getObjects()) {
            switch (object) {
                case MANAGE_INVOICE -> {
                    for (ManageInvoiceDto invoice : this.manageInvoiceService.findAllToReplicate()) {
                        this.producerReplicateManageInvoiceService.create(invoice);
                    }
                }
                default ->
                    System.out.println("Número inválido. Por favor, intenta de nuevo con un número del 1 al 7.");
            }
        }
    }
}
