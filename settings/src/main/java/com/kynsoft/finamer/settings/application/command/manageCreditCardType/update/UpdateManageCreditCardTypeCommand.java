package com.kynsoft.finamer.settings.application.command.manageCreditCardType.update;

import com.kynsoft.finamer.settings.application.command.manageCreditCardType.create.*;
import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageCreditCardTypeCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;
    private Integer firstDigit;
    private Status status;

    public UpdateManageCreditCardTypeCommand(UUID id, String name, String description, Integer firstDigit, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.firstDigit = firstDigit;
        this.status = status;
    }

    public static UpdateManageCreditCardTypeCommand fromRequest(UpdateManageCreditCardTypeRequest request, UUID id) {
        return new UpdateManageCreditCardTypeCommand(
                id,
                request.getName(),
                request.getDescription(),
                request.getFirstDigit(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageCreditCardTypeMessage(id);
    }
}
