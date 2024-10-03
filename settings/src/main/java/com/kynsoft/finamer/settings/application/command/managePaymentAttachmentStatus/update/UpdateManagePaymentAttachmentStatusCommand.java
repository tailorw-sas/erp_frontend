package com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateManagePaymentAttachmentStatusCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private Status status;
    private List<UUID> navigate;
    private UUID module;
    private Boolean show;
    private Boolean defaults;
    private String permissionCode;
    private String description;
    private boolean nonNone;
    private boolean patWithAttachment;
    private boolean pwaWithOutAttachment;

    public UpdateManagePaymentAttachmentStatusCommand(UUID id, String code, String name, Status status, List<UUID> navigate,
                                                      UUID module, Boolean show, Boolean defaults, String permissionCode, String description, 
                                                      boolean nonNone, boolean patWithAttachment, boolean pwaWithOutAttachment) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.navigate = navigate;
        this.module = module;
        this.show = show;
        this.defaults = defaults;
        this.permissionCode = permissionCode;
        this.description = description;
        this.nonNone = nonNone;
        this.patWithAttachment = patWithAttachment;
        this.pwaWithOutAttachment = pwaWithOutAttachment;
    }
    
    public static UpdateManagePaymentAttachmentStatusCommand fromRequest(UpdateManagePaymentAttachmentStatusRequest request,
                                                                         UUID id){
        return new UpdateManagePaymentAttachmentStatusCommand(id, request.getCode(), request.getName(),
                request.getStatus(), request.getNavigate(), request.getModule(), request.getShow(), request.getDefaults(),
                request.getPermissionCode(), request.getDescription(), request.isNonNone(), request.isPatWithAttachment(), request.isPwaWithOutAttachment());
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagePaymentAttachmentStatusMessage(id);
    }
}
