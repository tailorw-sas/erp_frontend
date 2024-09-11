package com.kynsoft.finamer.invoicing.application.command.manageB2BPartnerType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageB2BPartnerTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;

    public CreateManageB2BPartnerTypeCommand(String code, String description, String name, Status status) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.name = name;
        this.status = status;
    }

    public static CreateManageB2BPartnerTypeCommand fromRequest(CreateManageB2BPartnerTypeRequest request) {
        return new CreateManageB2BPartnerTypeCommand(
                request.getCode(),
                request.getDescription(),
                request.getName(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageB2BPartnerTypeMessage(id);
    }
}
