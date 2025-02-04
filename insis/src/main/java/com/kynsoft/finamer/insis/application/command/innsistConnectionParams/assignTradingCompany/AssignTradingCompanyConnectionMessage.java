package com.kynsoft.finamer.insis.application.command.innsistConnectionParams.assignTradingCompany;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AssignTradingCompanyConnectionMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "ASSOCIATE_CONNECTION_PARAM_TO_TRADING_COMPANY";

    public AssignTradingCompanyConnectionMessage(UUID id){
        this.id = id;
    }
}
