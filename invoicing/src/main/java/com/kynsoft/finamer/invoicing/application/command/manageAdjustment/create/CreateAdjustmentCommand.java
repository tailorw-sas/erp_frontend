package com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateAdjustmentCommand implements ICommand {

    private UUID id;
    private Double amount;
    private LocalDateTime date;
    private String description;
    private UUID transactionType;
    private UUID paymentTransactionType;
    private UUID roomRate;
    private String employee;

    public CreateAdjustmentCommand(Double amount, LocalDateTime date, String description, UUID transactionType,
            UUID paymentTransactionType,
            UUID roomRate, String employee) {
        this.id = UUID.randomUUID();
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.transactionType = transactionType;
        this.roomRate = roomRate;
        this.employee = employee;
        this.paymentTransactionType = paymentTransactionType;
    }

    public static CreateAdjustmentCommand fromRequest(CreateAdjustmentRequest request) {
        return new CreateAdjustmentCommand(
                request.getAmount(),
                request.getDate(),
                request.getDescription(),
                request.getTransactionType(),
                request.getPaymentTransactionType(),
                request.getRoomRate(),
                request.getEmployee());
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateAdjustmentMessage(id);
    }
}
