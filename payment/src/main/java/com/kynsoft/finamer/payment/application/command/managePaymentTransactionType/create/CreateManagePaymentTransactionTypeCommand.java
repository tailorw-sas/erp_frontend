package com.kynsoft.finamer.payment.application.command.managePaymentTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagePaymentTransactionTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private Boolean cash;
    private Boolean deposit;
    private Boolean applyDeposit;
    private Boolean remarkRequired;
    private Integer minNumberOfCharacter;

    public CreateManagePaymentTransactionTypeCommand(UUID id, String code, String name, 
                                                     Boolean cash, Boolean deposit, Boolean applyDeposit, 
                                                     Boolean remarkRequired, Integer minNumberOfCharacter) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.cash = cash;
        this.deposit = deposit;
        this.applyDeposit = applyDeposit;
        this.remarkRequired = remarkRequired;
        this.minNumberOfCharacter = minNumberOfCharacter;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagePaymentTransactionTypeMessage(id);
    }
}
