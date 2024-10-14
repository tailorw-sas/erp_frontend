package com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateManagePaymentAttachmentStatusCommand implements ICommand {

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
    private boolean supported;
    
    public CreateManagePaymentAttachmentStatusCommand(final String code, final String name, final Status status,
                                                      final List<UUID> navigate, final UUID module, final Boolean show, Boolean defaults,
                                                      final String permissionCode, final String description, boolean nonNone, boolean patWithAttachment, 
                                                      boolean pwaWithOutAttachment, boolean supported) {
        this.id = UUID.randomUUID();
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
        this.supported = supported;
    }

    public static CreateManagePaymentAttachmentStatusCommand fromRequest(CreateManagePaymentAttachmentStatusRequest request){
        return new CreateManagePaymentAttachmentStatusCommand(
                request.getCode(), 
                request.getName(), 
                request.getStatus(), 
                request.getNavigate(), 
                request.getModule(), 
                request.getShow(), 
                request.getDefaults(), 
                request.getPermissionCode(), 
                request.getDescription(),
                request.isNonNone(),
                request.isPatWithAttachment(),
                request.isPwaWithOutAttachment(),
                request.isSupported()
        );
    }
    @Override
    public ICommandMessage getMessage() {
        return new CreateManagePaymentAttachmentStatusMessage(id);
    }
}
