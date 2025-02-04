package com.kynsoft.finamer.insis.application.command.innsistConnectionParams.unassingTradingCompany;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UnassingTradingCompanyConnectionCommand implements ICommand {

    private UUID tradingCompanyId;

    public UnassingTradingCompanyConnectionCommand(UUID tradingCompanyId){
        this.tradingCompanyId = tradingCompanyId;
    }

    public static UnassingTradingCompanyConnectionCommand fromRequest(UnassingTradingCompanyConnectionRequest request){
        return new UnassingTradingCompanyConnectionCommand(request.getTradingCompanyId());
    }

    @Override
    public ICommandMessage getMessage() {
        return new UnassingTradingCompanyConnectionMessage(tradingCompanyId);
    }
}
