package com.kynsoft.finamer.invoicing.application.command.manageCityState.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageCityStateCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private UUID country;
    private UUID timeZone;

    public CreateManageCityStateCommand(String code, String name, String description, Status status, UUID country, UUID timeZone) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.description = description;
        this.status = status;
        this.country = country;
        this.timeZone = timeZone;
    }

    public static CreateManageCityStateCommand fromRequest(CreateManageCityStateRequest request) {
        return new CreateManageCityStateCommand(
                request.getCode(),
                request.getName(),
                request.getDescription(),
                request.getStatus(),
                request.getCountry(),
                request.getTimeZone()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageCityStateMessage(id);
    }
}
