package com.kynsoft.finamer.payment.application.command.manageAgency.create;

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
    private String status;
    private UUID agencyType;
    private UUID client;

    public CreateManageAgencyCommand(UUID id, String code, String name, String status, UUID agencyType,UUID client) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.agencyType = agencyType;
        this.client = client;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageAgencyMessage(id);
    }
}
