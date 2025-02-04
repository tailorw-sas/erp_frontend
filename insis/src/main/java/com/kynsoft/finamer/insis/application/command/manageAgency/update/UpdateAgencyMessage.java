package com.kynsoft.finamer.insis.application.command.manageAgency.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateAgencyMessage implements ICommandMessage {
    private final UUID id;

    private final String command = "CREATE_MANAGE_AGENCY";

    public UpdateAgencyMessage(UUID id){
        this.id = id;
    }
}
