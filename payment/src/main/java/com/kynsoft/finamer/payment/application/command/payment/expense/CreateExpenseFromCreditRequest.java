package com.kynsoft.finamer.payment.application.command.payment.expense;

import com.kynsoft.finamer.payment.application.command.payment.create.CreateAttachmentRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateExpenseFromCreditRequest {

    private UUID invoice;
    private UUID client;
    private UUID agency;
    private UUID hotel;
    private UUID employee;
    private List<CreateAttachmentRequest> attachments;
}
