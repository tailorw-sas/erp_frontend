package com.kynsoft.finamer.insis.application.command.innsistConnectionParams.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateConnectionParamsMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_INNSIST_CONNECTION_PARAMETERS";

    public UpdateConnectionParamsMessage(UUID id){
        this.id = id;
    }

}
