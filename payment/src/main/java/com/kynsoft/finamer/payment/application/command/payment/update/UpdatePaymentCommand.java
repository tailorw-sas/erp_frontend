package com.kynsoft.finamer.payment.application.command.payment.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdatePaymentCommand implements ICommand {
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
    private Double paymentBalance;
    private Double depositAmount;
    private Double depositBalance;
    private Double otherDeductions;
    private Double identified;
    private Double notIdentified;
    private String remark;
    private Status status;
    private UUID employee;

    public UpdatePaymentCommand(UUID id, UUID paymentSource, String reference, LocalDate transactionDate, 
                                UUID paymentStatus, UUID client, UUID agency, UUID hotel, UUID bankAccount, 
                                UUID attachmentStatus, Double paymentAmount, Double paymentBalance, Double depositAmount, 
                                Double depositBalance, Double otherDeductions, Double identified, Double notIdentified, 
                                String remark, Status status, UUID employee) {
        this.id = id;
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
        this.paymentBalance = paymentBalance;
        this.depositAmount = depositAmount;
        this.depositBalance = depositBalance;
        this.otherDeductions = otherDeductions;
        this.identified = identified;
        this.notIdentified = notIdentified;
        this.remark = remark;
        this.status = status;
        this.employee = employee;
    }

    public static UpdatePaymentCommand fromRequest(UpdatePaymentRequest request, UUID id) {
        return new UpdatePaymentCommand(
                id,
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
                request.getPaymentBalance(),
                request.getDepositAmount(),
                request.getDepositBalance(),
                request.getOtherDeductions(),
                request.getIdentified(),
                request.getNotIdentified(),
                request.getRemark(),
                request.getStatus(),
                request.getEmployee()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdatePaymentMessage(id);
    }
}
