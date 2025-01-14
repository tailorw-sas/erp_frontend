package com.kynsoft.finamer.insis.application.command.manageAgency.createMany;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.insis.application.command.manageAgency.create.CreateManageAgencyCommand;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateManyManageAgencyCommand implements ICommand {

    private List<CreateManageAgencyCommand> newAgencies;

    public CreateManyManageAgencyCommand(List<CreateManageAgencyCommand> newAgencies){
        this.newAgencies = newAgencies;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManyManageAgencyMessage();
    }
}
