package com.kynsoft.finamer.insis.application.command.innsistConnectionParams.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateConnectionParamsMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "CREATE_INNSIST_CONNECTION_PARAMETERS";

    public CreateConnectionParamsMessage(UUID id){
        this.id = id;
    }
}
