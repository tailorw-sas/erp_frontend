package com.kynsoft.finamer.payment.application.command.payment.expense;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.application.command.payment.create.CreateAttachmentRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateExpenseFromCreditCommand implements ICommand {

    private UUID id;
    private UUID invoice;
    private UUID client;
    private UUID agency;
    private UUID hotel;
    private UUID employee;
    private List<CreateAttachmentRequest> attachments;

    public CreateExpenseFromCreditCommand(UUID invoice,
                                          UUID client,
                                          UUID agency,
                                          UUID hotel,
                                          UUID employee,
                                          List<CreateAttachmentRequest> attachments){
        this.id = UUID.randomUUID();
        this.invoice = invoice;
        this.client = client;
        this.agency = agency;
        this.hotel = hotel;
        this.employee = employee;
        this.attachments = attachments;
    }

    public static CreateExpenseFromCreditCommand fromRequest(CreateExpenseFromCreditRequest request){
        return new CreateExpenseFromCreditCommand(
                request.getInvoice(),
                request.getClient(),
                request.getAgency(),
                request.getHotel(),
                request.getEmployee(),
                request.getAttachments()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateExpenseFromCreditMessage(this.id);
    }
}
