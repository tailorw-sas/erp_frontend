package com.tailorw.tcaInnsist.application.command.manageTradingCompany.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageTradingCompanyMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_MANAGE_TRADING_COMPANY_COMMAND";

    public CreateManageTradingCompanyMessage(UUID id){
        this.id = id;
    }
}
