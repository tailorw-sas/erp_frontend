package com.kynsoft.finamer.insis.application.command.innsistConnectionParams.unassingTradingCompany;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UnassingTradingCompanyConnectionMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "UNASSING_TRADING_COMPANY_CONNECTION";

    public UnassingTradingCompanyConnectionMessage(UUID id){
        this.id = id;
    }
}
