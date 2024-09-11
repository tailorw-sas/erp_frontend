package com.kynsoft.finamer.invoicing.application.command.manageCityState.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageCityStateCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;
    private Status status;
    private UUID country;
    private UUID timeZone;

    public UpdateManageCityStateCommand(UUID id, String name, String description, Status status, UUID country, UUID timeZone) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.country = country;
        this.timeZone = timeZone;
    }

    public static UpdateManageCityStateCommand fromRequest(UpdateManageCityStateRequest request, UUID id) {
        return new UpdateManageCityStateCommand(
                id,
                request.getName(),
                request.getDescription(),
                request.getStatus(),
                request.getCountry(),
                request.getTimeZone()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageCityStateMessage(id);
    }
}
