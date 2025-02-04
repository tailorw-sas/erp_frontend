package com.kynsoft.finamer.insis.application.command.manageTradingCompany.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateTradingCompanyMesssage implements ICommandMessage {
    private final UUID id;

    private final String command = "CREATE_TRADING_COMPANY";

    public CreateTradingCompanyMesssage(UUID id){
        this.id = id;
    }
}
