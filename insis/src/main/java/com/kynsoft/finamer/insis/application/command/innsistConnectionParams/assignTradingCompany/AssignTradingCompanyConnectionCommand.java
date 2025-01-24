package com.kynsoft.finamer.insis.application.command.innsistConnectionParams.assignTradingCompany;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AssignTradingCompanyConnectionCommand implements ICommand {
    private UUID connectionParmId;
    private UUID tradingCompanyId;

    public AssignTradingCompanyConnectionCommand(UUID connectionParmId, UUID tradingCompanyId){
        this.connectionParmId = connectionParmId;
        this.tradingCompanyId = tradingCompanyId;
    }

    public static AssignTradingCompanyConnectionCommand fromRequest(AssignTradingCompanyConnectionRequest request){
        return new AssignTradingCompanyConnectionCommand(request.getConnectionParmId(), request.getTradingCompanyId());
    }
    @Override
    public ICommandMessage getMessage() {
        return new AssignTradingCompanyConnectionMessage(connectionParmId);
    }
}
