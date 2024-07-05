package com.kynsoft.finamer.invoicing.application.command.manageAgency.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageAgencyCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;


    public CreateManageAgencyCommand(UUID id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public static CreateManageAgencyCommand fromRequest(CreateManageAgencyRequest request) {
        return new CreateManageAgencyCommand(
               request.getId(),  request.getCode(),  request.getName()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageAgencyMessage(id);
    }
}
