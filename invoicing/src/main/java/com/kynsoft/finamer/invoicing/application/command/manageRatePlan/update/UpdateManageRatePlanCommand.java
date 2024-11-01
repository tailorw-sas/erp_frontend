package com.kynsoft.finamer.invoicing.application.command.manageRatePlan.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageRatePlanCommand implements ICommand {

    private UUID id;
    private String name;
    private String status;

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageRatePlanMessage(id);
    }
}
