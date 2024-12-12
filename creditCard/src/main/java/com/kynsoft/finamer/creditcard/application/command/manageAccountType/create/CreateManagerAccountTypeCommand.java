package com.kynsoft.finamer.creditcard.application.command.manageAccountType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManagerAccountTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private boolean moduleVcc;
    private boolean modulePayment;

    public CreateManagerAccountTypeCommand(String code, String description, String name, Status status, boolean moduleVcc, boolean modulePayment) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.name = name;
        this.status = status;
        this.moduleVcc = moduleVcc;
        this.modulePayment = modulePayment;
    }

    public static CreateManagerAccountTypeCommand fromRequest(CreateManagerAccountTypeRequest request) {
        return new CreateManagerAccountTypeCommand(
                request.getCode(),
                request.getDescription(),
                request.getName(),
                request.getStatus(),
                request.isModuleVcc(),
                request.isModulePayment()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagerAccountTypeMessage(id);
    }
}
