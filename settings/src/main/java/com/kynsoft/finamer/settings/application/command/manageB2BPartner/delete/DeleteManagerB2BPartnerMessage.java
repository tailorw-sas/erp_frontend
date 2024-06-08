package com.kynsoft.finamer.settings.application.command.manageB2BPartner.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManagerB2BPartnerMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGER_B2B_PARTNER";

    public DeleteManagerB2BPartnerMessage(UUID id) {
        this.id = id;
    }

}
