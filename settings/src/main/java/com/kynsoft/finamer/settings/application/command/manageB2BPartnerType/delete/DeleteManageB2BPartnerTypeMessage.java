package com.kynsoft.finamer.settings.application.command.manageB2BPartnerType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageB2BPartnerTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_B2B_PARTNER_TYPE";

    public DeleteManageB2BPartnerTypeMessage(UUID id) {
        this.id = id;
    }

}
