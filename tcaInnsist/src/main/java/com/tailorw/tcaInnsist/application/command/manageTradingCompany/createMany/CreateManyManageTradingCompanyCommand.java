package com.tailorw.tcaInnsist.application.command.manageTradingCompany.createMany;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.tailorw.tcaInnsist.application.command.manageTradingCompany.create.CreateManageTradingCompanyCommand;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateManyManageTradingCompanyCommand implements ICommand {

    private List<CreateManageTradingCompanyCommand> createManageTradingCompanyCommands;

    public CreateManyManageTradingCompanyCommand(List<CreateManageTradingCompanyCommand> createManageTradingCompanyCommands){
        this.createManageTradingCompanyCommands = createManageTradingCompanyCommands;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManyManageTradingCompanyMessage();
    }
}
