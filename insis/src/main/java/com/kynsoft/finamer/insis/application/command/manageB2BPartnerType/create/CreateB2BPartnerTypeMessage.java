package com.kynsoft.finamer.insis.application.command.manageB2BPartnerType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateB2BPartnerTypeMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_MANAGE_B2BPARTNER_TYPE";

    public CreateB2BPartnerTypeMessage(UUID id){
        this.id = id;
    }
}
