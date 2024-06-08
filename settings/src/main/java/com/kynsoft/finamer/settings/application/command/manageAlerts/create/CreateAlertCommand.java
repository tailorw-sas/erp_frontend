package com.kynsoft.finamer.settings.application.command.manageAlerts.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateAlertCommand implements ICommand {
    
    private UUID id;
    private String code;
    private String name;
    private Boolean popup;
    private Status status;
    private String description;
    
    public CreateAlertCommand(final String code, final String name, final Boolean popup, final Status status, final String description) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.popup = popup;
        this.status = status;
        this.description = description;
    }
    
    public static CreateAlertCommand fromRequest(CreateAlertRequest request) {
        return new CreateAlertCommand(request.getCode(), request.getName(), request.getPopup(), request.getStatus(), request.getDescription());
    }
    
    @Override
    public ICommandMessage getMessage() {
        return new CreateAlertMessage(id);
    }
}
