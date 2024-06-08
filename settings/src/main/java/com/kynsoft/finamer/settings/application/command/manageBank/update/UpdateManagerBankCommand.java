package com.kynsoft.finamer.settings.application.command.manageBank.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagerBankCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;
    private Status status;

    public UpdateManagerBankCommand(UUID id, String description, String name, Status status) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.status = status;
    }

    public static UpdateManagerBankCommand fromRequest(UpdateManagerBankRequest request, UUID id) {
        return new UpdateManagerBankCommand(
                id,
                request.getDescription(),
                request.getName(), 
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagerBankMessage(id);
    }
}
