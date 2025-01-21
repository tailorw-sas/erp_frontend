package com.tailorw.tcaInnsist.application.command.manageTradingCompany.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateManyManageTradingCompanyMessage implements ICommandMessage {
    private final String command = "CREATE_MANY_MANAGE_TRADING_COMPANY_COMMAND";

    public CreateManyManageTradingCompanyMessage(){}
}
