package com.kynsoft.finamer.invoicing.application.command.manageAdjustment.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateAdjustmentCommand implements ICommand {

    private UUID id;
    private Double amount;
    private LocalDateTime date;
    private String description;
    private UUID transactionType;
    private UUID roomRate;

    public UpdateAdjustmentCommand(UUID id, Double amount, LocalDateTime date, String description, UUID transactionType,  UUID roomRate) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.transactionType = transactionType;
        this.roomRate = roomRate;
    }

    public static UpdateAdjustmentCommand fromRequest(UpdateAdjustmentRequest request, UUID id) {
        return new UpdateAdjustmentCommand(
                id,
                request.getAmount(),
                request.getDate(),
                request.getDescription(),
                request.getTransactionType(),
                request.getRoomRate()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateAdjustmentMessage(id);
    }
}
