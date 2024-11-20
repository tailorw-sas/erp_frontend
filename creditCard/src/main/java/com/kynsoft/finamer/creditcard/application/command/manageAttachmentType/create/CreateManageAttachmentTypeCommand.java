package com.kynsoft.finamer.creditcard.application.command.manageAttachmentType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageAttachmentTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private boolean defaults;

    public CreateManageAttachmentTypeCommand(String code, String description, Status status, String name, boolean defaults) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.status = status;
        this.name = name;
        this.defaults = defaults;
    }

    public static CreateManageAttachmentTypeCommand fromRequest(CreateManageAttachmentTypeRequest request){
        return new CreateManageAttachmentTypeCommand(
                request.getCode(),
                request.getDescription(),
                request.getStatus(),
                request.getName(),
                request.isDefaults()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageAttachmentTypeMessage(id);
    }
}
