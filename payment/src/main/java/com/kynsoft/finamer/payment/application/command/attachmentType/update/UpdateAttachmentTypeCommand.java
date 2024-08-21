package com.kynsoft.finamer.payment.application.command.attachmentType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateAttachmentTypeCommand implements ICommand {
    private UUID id;
    private String name;
    private String description;
    private Status status;
    private Boolean defaults;
    private boolean antiToIncomeImport;

    public UpdateAttachmentTypeCommand(UUID id, String name, String description, Status status, Boolean defaults, boolean antiToIncomeImport) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.defaults = defaults;
        this.antiToIncomeImport=antiToIncomeImport;
    }

    public static UpdateAttachmentTypeCommand fromRequest(UpdateAttachmentTypeRequest request, UUID id) {
        return new UpdateAttachmentTypeCommand(
                id,
                request.getName(),
                request.getDescription(),
                request.getStatus(),
                request.getDefaults(),
                request.isAntiToIncomeImport()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateAttachmentTypeMessage(id);
    }
}
