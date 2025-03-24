package com.kynsoft.finamer.payment.application.command.manageAgency.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageAgencyCommand implements ICommand {

    private UUID id;
    private String name;
    private String status;  
    private UUID agencyType;
    private UUID client;
    private UUID country;

    public UpdateManageAgencyCommand(UUID id,
                                     String name,
                                     String status,
                                     UUID agencyType,
                                     UUID client,
                                     UUID country){
        this.id = id;
        this.name = name;
        this.status = status;
        this.agencyType = agencyType;
        this.client = client;
        this.country = country;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageAgencyMessage(id);
    }
}
