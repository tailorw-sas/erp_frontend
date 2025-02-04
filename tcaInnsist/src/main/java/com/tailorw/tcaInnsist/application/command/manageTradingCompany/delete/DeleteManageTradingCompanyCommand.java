package com.tailorw.tcaInnsist.application.command.manageTradingCompany.delete;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageTradingCompanyCommand implements ICommand {

    private UUID id;

    public DeleteManageTradingCompanyCommand(UUID id){
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteManageTradingCompanyMessage();
    }
}
