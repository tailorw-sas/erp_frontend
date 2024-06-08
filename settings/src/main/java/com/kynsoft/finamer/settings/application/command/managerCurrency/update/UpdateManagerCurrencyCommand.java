package com.kynsoft.finamer.settings.application.command.managerCurrency.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagerCurrencyCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;
    private Status status;

    public UpdateManagerCurrencyCommand(UUID id, String description, String name, Status status) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.status = status;
    }

    public static UpdateManagerCurrencyCommand fromRequest(UpdateManagerCurrencyRequest request, UUID id) {
        return new UpdateManagerCurrencyCommand(
                id,
                request.getDescription(),
                request.getName(), 
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagerCurrencyMessage(id);
    }
}
