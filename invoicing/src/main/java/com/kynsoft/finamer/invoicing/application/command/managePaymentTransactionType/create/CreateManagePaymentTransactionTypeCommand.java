package com.kynsoft.finamer.invoicing.application.command.managePaymentTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class CreateManagePaymentTransactionTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String status;
    private Boolean cash;
    private Boolean deposit;
    private Boolean applyDeposit;
    private Boolean remarkRequired;
    private Integer minNumberOfCharacter;
    private String defaultRemark;
    private boolean defaults;

    public CreateManagePaymentTransactionTypeCommand(UUID id, String code, String name, String status,
                                                     Boolean cash, Boolean deposit, Boolean applyDeposit, 
                                                     Boolean remarkRequired, Integer minNumberOfCharacter,
                                                     String defaultRemark,Boolean defaults) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.cash = cash;
        this.deposit = deposit;
        this.applyDeposit = applyDeposit;
        this.remarkRequired = remarkRequired;
        this.minNumberOfCharacter = minNumberOfCharacter;
        this.defaultRemark = defaultRemark;
        this.defaults= Objects.nonNull(defaults)?defaults:false;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagePaymentTransactionTypeMessage(id);
    }
}
