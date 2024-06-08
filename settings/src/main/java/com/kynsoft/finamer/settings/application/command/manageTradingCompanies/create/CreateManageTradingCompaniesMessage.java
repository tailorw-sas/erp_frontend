package com.kynsoft.finamer.settings.application.command.manageTradingCompanies.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageTradingCompaniesMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_TRADING_COMPANIES";

    public CreateManageTradingCompaniesMessage(UUID id) {
        this.id = id;
    }
}
