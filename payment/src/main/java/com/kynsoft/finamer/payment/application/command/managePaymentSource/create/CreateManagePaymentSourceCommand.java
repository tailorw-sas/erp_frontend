package com.kynsoft.finamer.payment.application.command.managePaymentSource.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagePaymentSourceCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String status;
    private Boolean expense;

    public CreateManagePaymentSourceCommand(UUID id, String code, String name, String status, Boolean expense) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.expense = expense;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagePaymentSourceMessage(id);
    }
}
