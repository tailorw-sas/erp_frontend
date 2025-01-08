package com.kynsoft.finamer.insis.application.command.batchProcessLog.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateBatchProcessLogCommand implements ICommand {

    private UUID id;
    private String type;
    private String status;
    private LocalDateTime startedAt;
    private String hotel;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID processId;

    public CreateBatchProcessLogCommand(UUID id,
                                        String type,
                                        String status,
                                        LocalDateTime startedAt,
                                        String hotel,
                                        LocalDate startDate,
                                        LocalDate endDate,
                                        UUID processId){
        this.id = id;
        this.type = type;
        this.status = status;
        this.startedAt = startedAt;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.processId = processId;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateBatchProcessLogMessage(id);
    }
}
