package com.kynsoft.finamer.settings.application.command.manageCreditCardType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageCreditCardTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Integer firstDigit;
    private Status status;

    public CreateManageCreditCardTypeCommand(String code, String name, String description, Integer firstDigit, Status status) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.description = description;
        this.firstDigit = firstDigit;
        this.status = status;
    }

    public static CreateManageCreditCardTypeCommand fromRequest(CreateManageCreditCardTypeRequest request) {
        return new CreateManageCreditCardTypeCommand(
                request.getCode(),
                request.getName(),
                request.getDescription(),
                request.getFirstDigit(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagerCreditCardTypeMessage(id);
    }
}
