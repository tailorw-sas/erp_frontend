package com.kynsoft.finamer.settings.application.command.managerCurrency.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagerCurrencyCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;

    public CreateManagerCurrencyCommand(String code, String description, String name, Status status) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.name = name;
        this.status = status;
    }

    public static CreateManagerCurrencyCommand fromRequest(CreateManagerCurrencyRequest request) {
        return new CreateManagerCurrencyCommand(
                request.getCode(),
                request.getDescription(),
                request.getName(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagerCurrencyMessage(id);
    }
}
