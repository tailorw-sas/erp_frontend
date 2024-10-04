package com.kynsoft.finamer.invoicing.application.command.manageCurrency.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageCurrencyCommand implements ICommand {

    private UUID id;
    private String name;
    private String status;

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageCurrencyMessage(id);
    }
}
