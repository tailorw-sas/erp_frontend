package com.kynsoft.finamer.payment.application.command.payment.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentCommand implements ICommand {

    private UUID id;
    private UUID paymentSource;
    private String reference;
    private LocalDate transactionDate;
    private UUID paymentStatus;
    private UUID client;
    private UUID agency;
    private UUID hotel;
    private UUID bankAccount;
    private UUID attachmentStatus;

    private Double paymentAmount;
    private String remark;
    private Status status;

    private PaymentDto payment;
    private List<CreateAttachmentRequest> attachments;

    public CreatePaymentCommand(Status status, UUID paymentSource, String reference, LocalDate transactionDate, 
                                UUID paymentStatus, UUID client, UUID agency, UUID hotel, UUID bankAccount, 
                                UUID attachmentStatus, Double paymentAmount, String remark, List<CreateAttachmentRequest> attachments) {
        this.id = UUID.randomUUID();
        this.status = status;
        this.paymentSource = paymentSource;
        this.reference = reference;
        this.transactionDate = transactionDate;
        this.paymentStatus = paymentStatus;
        this.client = client;
        this.agency = agency;
        this.hotel = hotel;
        this.bankAccount = bankAccount;
        this.attachmentStatus = attachmentStatus;
        this.paymentAmount = paymentAmount;
        this.remark = remark;
        this.attachments = attachments;
    }

    public static CreatePaymentCommand fromRequest(CreatePaymentRequest request) {
        return new CreatePaymentCommand(
                request.getStatus(),
                request.getPaymentSource(),
                request.getReference(),
                request.getTransactionDate(),
                request.getPaymentStatus(),
                request.getClient(),
                request.getAgency(),
                request.getHotel(),
                request.getBankAccount(),
                request.getAttachmentStatus(),
                request.getPaymentAmount(),
                request.getRemark(),
                request.getAttachments()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePaymentMessage(payment);
    }
}
