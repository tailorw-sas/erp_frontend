package com.kynsoft.finamer.insis.application.command.batchProcessLog.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.BatchProcessLogDto;
import com.kynsoft.finamer.insis.domain.services.IBatchProcessLogService;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchStatus;
import org.springframework.stereotype.Component;

@Component
public class UpdateBatchProcessLogCommandHandler implements ICommandHandler<UpdateBatchProcessLogCommand> {

    private final IBatchProcessLogService service;

    public UpdateBatchProcessLogCommandHandler(IBatchProcessLogService service){
        this.service = service;
    }

    @Override
    public void handle(UpdateBatchProcessLogCommand command) {
        BatchProcessLogDto batchProcessLogDto = service.findById(command.getId());

        batchProcessLogDto.setHotel(command.getHotel());
        batchProcessLogDto.setStatus(BatchStatus.convertToBatchStatus(command.getStatus()));
        batchProcessLogDto.setCompletedAt(command.getCompletedAt());
        //batchProcessLogDto.setTotalRecordsRead(command.getTotalRecordsRead());
        //batchProcessLogDto.setTotalRecordsProcessed(command.getTotalRecordsProcessed());

        service.update(batchProcessLogDto);
    }
}
