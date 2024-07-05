package com.kynsoft.finamer.invoicing.application.command.manageTradingCompanies.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageTradingCompaniesMessage implements ICommandMessage {
    private final UUID id;

    private final String command = "DELETE_MANAGE_TRADING_COMPANIES";

    public DeleteManageTradingCompaniesMessage(UUID id) {
        this.id = id;
    }
}
