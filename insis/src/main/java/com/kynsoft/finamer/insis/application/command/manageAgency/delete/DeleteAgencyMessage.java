package com.kynsoft.finamer.insis.application.command.manageAgency.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteAgencyMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_MANAGE_AGENCY";

    public DeleteAgencyMessage(UUID id){
        this.id = id;
    }
}
