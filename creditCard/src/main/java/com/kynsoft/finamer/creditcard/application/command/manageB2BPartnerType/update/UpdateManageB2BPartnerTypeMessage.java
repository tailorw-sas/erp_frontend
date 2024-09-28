package com.kynsoft.finamer.creditcard.application.command.manageB2BPartnerType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageB2BPartnerTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_B2B_PARTNER_TYPE";

    public UpdateManageB2BPartnerTypeMessage(UUID id) {
        this.id = id;
    }

}
