package com.kynsoft.finamer.settings.application.command.manageAlerts.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.application.command.manageAlerts.create.CreateAlertRequest;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateAlertCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private Boolean popup;
    private Status status;
    private String description;
    
    public UpdateAlertCommand(UUID id, String code, String name, Boolean popup, Status status, String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.popup = popup;
        this.status = status;
        this.description = description;
    }
    
    public static UpdateAlertCommand fromRequest(CreateAlertRequest request, UUID id) {
        return new UpdateAlertCommand(id, request.getCode(), request.getName(), request.getPopup(), request.getStatus(), request.getDescription());
    }
    
    @Override
    public ICommandMessage getMessage() {
        return new UpdateAlertMessage(id);
    }
}
