package com.kynsoft.finamer.payment.application.command.paymentDetail.undoReverseTransaction;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateUndoReverseTransactionCommand implements ICommand {

    private Long reverseFromParentId;
    private final IMediator mediator;
    private UUID employee;

    public CreateUndoReverseTransactionCommand(Long reverseFromParentId, IMediator mediator, UUID employee) {
        this.reverseFromParentId = reverseFromParentId;
        this.mediator = mediator;
        this.employee = employee;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateUndoReverseTransactionMessage(reverseFromParentId);
    }
}
