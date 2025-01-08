package com.kynsoft.finamer.insis.application.command.manageAgency.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageAgencyMessage implements ICommandMessage {
    private final UUID id;

    private final String command = "UPDATE_MANAGE_AGENCY";

    public CreateManageAgencyMessage(UUID id){
        this.id = id;
    }
}
