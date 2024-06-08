package com.kynsof.identity.application.command.geographiclocations.create;

import com.kynsof.identity.domain.dto.enumType.GeographicLocationType;
import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateGeographicLocationCommand implements ICommand {
    private UUID id;
    private String name;
    private GeographicLocationType type;
    private UUID parent;


    public CreateGeographicLocationCommand(String name, GeographicLocationType type, UUID parent) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.type = type;
        this.parent = parent;
    }

    public static CreateGeographicLocationCommand fromRequest(CreateGeographicLocationRequest request) {
        return new CreateGeographicLocationCommand(
                request.getName(),
                request.getType(),
                request.getParent()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateGeographicLocationMessage(id);
    }
}
