package com.kynsoft.finamer.insis.application.command.manageRatePlan.createMany;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.insis.application.command.manageRatePlan.create.CreateRatePlanCommand;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateManyManageRatePlanCommand implements ICommand {

    private UUID hotel;
    private List<CreateRatePlanCommand> ratePlanCommands;

    public CreateManyManageRatePlanCommand(UUID hotel, List<CreateRatePlanCommand> ratePlanCommands){
        this.hotel = hotel;
        this.ratePlanCommands = ratePlanCommands;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManyManageRatePlanMessage();
    }
}
