package com.kynsoft.finamer.settings.application.command.manageVCCTransactionType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageVCCTransactionTypeCommand implements ICommand {

    private UUID id;
    private Status status;
    private String name;
    private String description;
    private Boolean isActive;

    public UpdateManageVCCTransactionTypeCommand(UUID id,  Status status, String name, String description, Boolean isActive) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
    }

    public static UpdateManageVCCTransactionTypeCommand fromRequest(UpdateManageVCCTransactionTypeRequest request, UUID id){
        return new UpdateManageVCCTransactionTypeCommand(
                id,
                request.getStatus(),
                request.getName(),
                request.getDescription(),
                request.getIsActive()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageVCCTransactionTypeMessage(id);
    }
}
