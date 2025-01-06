package com.kynsoft.finamer.insis.application.command.manageAgency.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class CreateManageAgencyCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String status;
    private boolean deleted;
    private String agencyAlias;

    public CreateManageAgencyCommand(UUID id, String code, String name, String agencyAlias, String status){
        this.id = Objects.nonNull(id) ? id : UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.status = status;
        this.deleted = false;
        this.agencyAlias = agencyAlias;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageAgencyMessage(id);
    }
}
