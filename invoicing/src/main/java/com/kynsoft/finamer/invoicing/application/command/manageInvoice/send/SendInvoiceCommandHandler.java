package com.kynsoft.finamer.invoicing.application.command.manageInvoice.send;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageBooking;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRoomRate;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Component
@Transactional
public class SendInvoiceCommandHandler implements ICommandHandler<SendInvoiceCommand> {

    private final IManageInvoiceService service;
    public SendInvoiceCommandHandler(IManageInvoiceService service) {
        this.service = service;
    }

    @Override
    @Transactional
    public void handle(SendInvoiceCommand command) {

        ManageInvoiceDto invoice = this.service.findById(command.getInvoice());
        command.setResult(true);
        }

}
