package com.kynsoft.finamer.invoicing.application.command.manageTimeZone.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagerTimeZoneCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Double elapse;
    private String status;

    public CreateManagerTimeZoneCommand(UUID id, String code, String description, String name, Double elapse, String status) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.name = name;
        this.elapse = elapse;
        this.status = status;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagerTimeZoneMessage(id);
    }
}
