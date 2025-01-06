package com.tailorw.finamer.scheduler.application.command.businessProcess.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateBusinessProcessMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_BUSINESS_PROCESS_COMMAND";

    public CreateBusinessProcessMessage(UUID id){
        this.id = id;
    }

}
