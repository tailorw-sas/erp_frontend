package com.kynsoft.finamer.insis.application.command.batchProcessLog.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.BatchProcessLogDto;
import com.kynsoft.finamer.insis.domain.services.IBatchProcessLogService;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchType;
import org.springframework.stereotype.Component;

@Component
public class CreateBatchProcessLogCommandHandler implements ICommandHandler<CreateBatchProcessLogCommand> {

    private final IBatchProcessLogService service;

    public CreateBatchProcessLogCommandHandler(IBatchProcessLogService service){
        this.service = service;
    }

    @Override
    public void handle(CreateBatchProcessLogCommand command) {
        BatchProcessLogDto dto = new BatchProcessLogDto(
                command.getId(),
                BatchType.convertToBatchType(command.getType()),
                BatchStatus.convertToBatchStatus(command.getStatus()),
                command.getStartedAt(),
                null,
                command.getHotel(),
                command.getStartDate(),
                command.getEndDate(),
                0,
                0,
                command.getProcessId()
        );

        service.create(dto);
    }
}
