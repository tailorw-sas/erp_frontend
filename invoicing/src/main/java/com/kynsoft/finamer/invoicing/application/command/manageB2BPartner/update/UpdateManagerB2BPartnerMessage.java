package com.kynsoft.finamer.invoicing.application.command.manageB2BPartner.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagerB2BPartnerMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGER_B2B_PARTNER";

    public UpdateManagerB2BPartnerMessage(UUID id) {
        this.id = id;
    }

}
