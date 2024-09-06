package com.kynsoft.finamer.payment.application.command.payment.createPaymentToCredit;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.payment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentToCreditCommand implements ICommand {

    private UUID id;
    private ManageInvoiceDto invoiceDto;
    private UUID client;
    private UUID agency;
    private UUID hotel;
    private List<CreateAttachmentRequest> attachments;
    private final IMediator mediator;

    public CreatePaymentToCreditCommand(UUID client, UUID agency, UUID hotel, ManageInvoiceDto invoiceDto, List<CreateAttachmentRequest> attachments, final IMediator mediator) {
        this.id = UUID.randomUUID();
        this.client = client;
        this.agency = agency;
        this.hotel = hotel;
        this.invoiceDto = invoiceDto;
        this.attachments = attachments;
        this.mediator = mediator;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePaymentToCreditMessage();
    }
}
