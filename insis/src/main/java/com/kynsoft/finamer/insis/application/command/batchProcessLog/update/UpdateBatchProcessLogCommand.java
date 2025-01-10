package com.kynsoft.finamer.insis.application.command.batchProcessLog.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateBatchProcessLogCommand implements ICommand {

    private UUID id;
    private String status;
    private String hotel;
    private LocalDateTime completedAt;
    private int totalRecordsRead;
    private int totalRecordsProcessed;

    public UpdateBatchProcessLogCommand(UUID id, String status, String hotel, LocalDateTime completedAt, int totalRecordsRead, int totalRecordsProcessed){
        this.id = id;
        this.status = status;
        this.hotel = hotel;
        this.completedAt = completedAt;
        this.totalRecordsRead = totalRecordsRead;
        this.totalRecordsProcessed = totalRecordsProcessed;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateBatchProcessLogMessage(id);
    }
}
