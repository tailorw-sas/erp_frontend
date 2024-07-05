package com.kynsoft.finamer.payment.application.command.managePaymentTransactionType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagePaymentTransactionTypeCommand implements ICommand {

    private UUID id;
    private String name;
    private String status;
    private Boolean cash;
    private Boolean deposit;
    private Boolean applyDeposit;
    private Boolean remarkRequired;
    private Integer minNumberOfCharacter;

    public UpdateManagePaymentTransactionTypeCommand(UUID id, String name, String status,
                                                     Boolean cash, Boolean deposit, Boolean applyDeposit, 
                                                     Boolean remarkRequired, Integer minNumberOfCharacter) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.cash = cash;
        this.deposit = deposit;
        this.applyDeposit = applyDeposit;
        this.remarkRequired = remarkRequired;
        this.minNumberOfCharacter = minNumberOfCharacter;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagePaymentTransactionTypeMessage(id);
    }
}
