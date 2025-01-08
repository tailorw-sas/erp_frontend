package com.tailorw.tcaInnsist.application.command.manageTradingCompany.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class DeleteManageTradingCompanyMessage implements ICommandMessage {
    private final String command = "DELETE_MANAGE_TRADING_COMPANY_COMMAND";

    public DeleteManageTradingCompanyMessage(){}
}
