package com.kynsoft.finamer.settings.application.command.managePaymentStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.application.command.managePaymentStatus.create.CreateManagePaymentStatusMessage;
import com.kynsoft.finamer.settings.application.command.managePaymentStatus.create.CreateManagePaymentStatusRequest;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdatePaymentStatusCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private Status status;
    private Boolean collected;
    private String description;
    
    public UpdatePaymentStatusCommand(UUID id, String code, String name, Status status, Boolean collected, String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.collected = collected;
        this.description = description;
    }
    
    public static UpdatePaymentStatusCommand fromRequest(UpdatePaymentStatusRequest request, UUID id) { 
        return new UpdatePaymentStatusCommand(id, request.getCode(), request.getName(), request.getStatus(), request.getCollected(), request.getDescription());
    }
    
    @Override
    public ICommandMessage getMessage() {
        return new UpdatePaymentStatusMessage(id);
    }
}
