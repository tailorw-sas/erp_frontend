package com.kynsoft.finamer.creditcard.application.command.manageMerchantCurrency.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManagerMerchantCurrencyCommand implements ICommand {

    private UUID id;
    private UUID managerMerchant;
    private UUID managerCurrency;
    private String value;
    private String description;
    private Status status;

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagerMerchantCurrencyMessage(id);
    }
}
