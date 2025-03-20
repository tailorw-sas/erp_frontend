package com.kynsoft.finamer.payment.application.command.manageCountry.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageCountryMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "CREATE_MANAGE_COUNTRY_COMMAND";

    public CreateManageCountryMessage(UUID id){
        this.id = id;
    }
}
