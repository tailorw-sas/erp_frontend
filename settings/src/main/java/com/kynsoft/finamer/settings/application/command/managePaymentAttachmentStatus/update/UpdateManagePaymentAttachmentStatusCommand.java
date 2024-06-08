package com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagePaymentAttachmentStatusCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private Status status;
    private String navigate;
    private String module;
    private Boolean show;
    private String permissionCode;
    private String description;

    public UpdateManagePaymentAttachmentStatusCommand(UUID id, String code, String name, Status status, String navigate, String module, Boolean show, String permissionCode, String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.navigate = navigate;
        this.module = module;
        this.show = show;
        this.permissionCode = permissionCode;
        this.description = description;
    }
    
    public static UpdateManagePaymentAttachmentStatusCommand fromRequest(UpdateManagePaymentAttachmentStatusRequest request, UUID id){
        return new UpdateManagePaymentAttachmentStatusCommand(id, request.getCode(), request.getName(), request.getStatus(), request.getNavigate(), request.getModule(), request.getShow(), request.getPermissionCode(), request.getDescription());
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagePaymentAttachmentStatusMessage(id);
    }
}
