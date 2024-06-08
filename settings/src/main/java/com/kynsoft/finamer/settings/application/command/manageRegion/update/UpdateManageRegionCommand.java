package com.kynsoft.finamer.settings.application.command.manageRegion.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageRegionCommand implements ICommand {

    private UUID id;
    private String description;
    private Status status;
    private String name;

    public static UpdateManageRegionCommand fromRequest(UpdateManageRegionRequest request, UUID id){
        return new UpdateManageRegionCommand(
                id,
                request.getDescription(),
                request.getStatus(),
                request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageRegionMessage(id);
    }

}
