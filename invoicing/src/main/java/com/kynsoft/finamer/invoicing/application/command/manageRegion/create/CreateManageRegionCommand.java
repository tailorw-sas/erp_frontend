package com.kynsoft.finamer.invoicing.application.command.manageRegion.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageRegionCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageRegionMessage(id);
    }

}
