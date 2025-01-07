package com.kynsoft.finamer.insis.application.command.batchProcessLog.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateBatchProcessLogMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "UPDATE_BATCH_PROCESS_LOG_COMMAND";

    public UpdateBatchProcessLogMessage(UUID id){
        this.id = id;
    }
}
