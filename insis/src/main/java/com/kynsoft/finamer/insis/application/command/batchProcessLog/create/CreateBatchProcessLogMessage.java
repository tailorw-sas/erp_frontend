package com.kynsoft.finamer.insis.application.command.batchProcessLog.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateBatchProcessLogMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_BATCH_PROCESS_LOG_COMMAND";

    public CreateBatchProcessLogMessage(UUID id){
        this.id = id;
    }
}
