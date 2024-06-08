package com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagePaymentAttachmentStatusCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private Status status;
    private String navigate;
    private String module;
    private Boolean show;
    private String permissionCode;
    private String description;
    
    public CreateManagePaymentAttachmentStatusCommand(final String code, final String name, final Status status, final String navigate, final String module, final Boolean show, final String permissionCode, final String description) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.status = status;
        this.navigate = navigate;
        this.module = module;
        this.show = show;
        this.permissionCode = permissionCode;
        this.description = description;
    }
    
    public static CreateManagePaymentAttachmentStatusCommand fromRequest(CreateManagePaymentAttachmentStatusRequest request){
        return new CreateManagePaymentAttachmentStatusCommand(request.getCode(), request.getName(), request.getStatus(), request.getNavigate(), request.getModule(), request.getShow(), request.getPermissionCode(), request.getDescription());
    }
    @Override
    public ICommandMessage getMessage() {
        return new CreateManagePaymentAttachmentStatusMessage(id);
    }
}
