package com.kynsoft.finamer.insis.application.command.manageB2BPartnerType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateB2BPartnerTypeMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_MANAGE_B2BPARTNER_TYPE";

    public UpdateB2BPartnerTypeMessage(UUID id){
        this.id = id;
    }
}
