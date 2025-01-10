package com.kynsoft.finamer.insis.application.command.innsistConnectionParams.assignTradingCompany;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AssignTradingCompanyConnectionRequest {
    public UUID tradingCompanyId;
    public UUID connectionParmId;
}
