package com.kynsoft.finamer.creditcard.application.command.manageB2BPartnerType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageB2BPartnerTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_B2B_PARTNER_TYPE";

    public CreateManageB2BPartnerTypeMessage(UUID id) {
        this.id = id;
    }

}
