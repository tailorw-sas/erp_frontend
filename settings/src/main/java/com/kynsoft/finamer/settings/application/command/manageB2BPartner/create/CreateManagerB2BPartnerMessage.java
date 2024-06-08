package com.kynsoft.finamer.settings.application.command.manageB2BPartner.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagerB2BPartnerMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGER_B2BPARTNER";

    public CreateManagerB2BPartnerMessage(UUID id) {
        this.id = id;
    }

}
