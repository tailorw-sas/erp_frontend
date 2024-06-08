package com.kynsoft.finamer.settings.application.command.manageVCCTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageVCCTransactionTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private Status status;
    private String name;
    private String description;
    private Boolean isActive;

    public CreateManageVCCTransactionTypeCommand(String code, Status status, String name, String description, Boolean isActive) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.status = status;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
    }

    public static CreateManageVCCTransactionTypeCommand fromRequest(
            CreateManageVCCTransactionTypeRequest request) {
        return new CreateManageVCCTransactionTypeCommand(
                request.getCode(),

                request.getStatus(),
                request.getName(),
                request.getDescription(),
                request.getIsActive()

        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageVCCTransactionTypeMessage(id);
    }
}
