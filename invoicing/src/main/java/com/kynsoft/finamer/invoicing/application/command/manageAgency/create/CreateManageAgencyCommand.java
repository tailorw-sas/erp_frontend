package com.kynsoft.finamer.invoicing.application.command.manageAgency.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EGenerationType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageAgencyCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private UUID client;
    private EGenerationType generationType;
    private String status;

    public CreateManageAgencyCommand(UUID id, String code, String name, UUID client,EGenerationType generationType,String status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.client = client;
        this.generationType=generationType;
        this.status=status;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageAgencyMessage(id);
    }
}
