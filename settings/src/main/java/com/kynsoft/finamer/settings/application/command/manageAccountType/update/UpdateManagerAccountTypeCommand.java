package com.kynsoft.finamer.settings.application.command.manageAccountType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagerAccountTypeCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;
    private Status status;
    private boolean moduleVcc;
    private boolean modulePayment;

    public UpdateManagerAccountTypeCommand(UUID id, String description, String name, Status status, boolean moduleVcc, boolean modulePayment) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.status = status;
        this.moduleVcc = moduleVcc;
        this.modulePayment = modulePayment;
    }

    public static UpdateManagerAccountTypeCommand fromRequest(UpdateManagerAccountTypeRequest request, UUID id) {
        return new UpdateManagerAccountTypeCommand(
                id,
                request.getDescription(),
                request.getName(), 
                request.getStatus(),
                request.isModuleVcc(),
                request.isModulePayment()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagerAccountTypeMessage(id);
    }
}
