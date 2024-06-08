package com.kynsoft.finamer.settings.application.command.manageRatePlan.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageRatePlanCommand implements ICommand {

    private UUID id;
    private String name;
    private String hotel;
    private String description;
    private Status status;

    public UpdateManageRatePlanCommand(UUID id, String description, String name, String hotel, Status status) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.hotel = hotel;
        this.status = status;
    }

    public static UpdateManageRatePlanCommand fromRequest(UpdateManageRatePlanRequest request, UUID id) {
        return new UpdateManageRatePlanCommand(
                id,
                request.getDescription(),
                request.getName(), 
                request.getHotel(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageRatePlanMessage(id);
    }
}
