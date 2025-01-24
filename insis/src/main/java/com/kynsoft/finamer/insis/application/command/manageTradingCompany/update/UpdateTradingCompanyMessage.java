package com.kynsoft.finamer.insis.application.command.manageTradingCompany.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateTradingCompanyMessage implements ICommandMessage {
    private final UUID id;

    private final String command = "UPDATE_MANAGE_TRADING_COMPANY";

    public UpdateTradingCompanyMessage(UUID id){
        this.id = id;
    }
}
