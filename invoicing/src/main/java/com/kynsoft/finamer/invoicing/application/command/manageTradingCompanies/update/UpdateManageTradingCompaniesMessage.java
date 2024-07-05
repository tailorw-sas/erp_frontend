package com.kynsoft.finamer.invoicing.application.command.manageTradingCompanies.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageTradingCompaniesMessage implements ICommandMessage {
    private final UUID id;

    private final String command = "UPDATE_MANAGE_TRADING_COMPANIES";

    public UpdateManageTradingCompaniesMessage(UUID id) {
        this.id = id;
    }
}
