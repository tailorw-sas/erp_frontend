package com.kynsoft.finamer.payment.application.command.manageInvoinceTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageInvoiceTransactionTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private Boolean negative;

    public CreateManageInvoiceTransactionTypeCommand(UUID id,
                                                     String code,
                                                     String name,
                                                     Boolean negative) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.negative = negative;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageInvoiceTransactionTypeMessage(id);
    }
}
