package com.kynsoft.finamer.payment.application.command.attachmentType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateAttachmentTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private Boolean defaults;
    private boolean antiToIncomeImport;

    public CreateAttachmentTypeCommand(String code, String name, String description, Status status, Boolean defaults,boolean antiToIncomeImport) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.description = description;
        this.status = status;
        this.defaults = defaults;
        this.antiToIncomeImport=antiToIncomeImport;
    }

    public static CreateAttachmentTypeCommand fromRequest(CreateAttachmentTypeRequest request) {
        return new CreateAttachmentTypeCommand(request.getCode(),  request.getName(), request.getDescription(),
                request.getStatus(), request.getDefaults(), request.isAntiToIncomeImport());
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateAttachmentTypeMessage(id);
    }
}
