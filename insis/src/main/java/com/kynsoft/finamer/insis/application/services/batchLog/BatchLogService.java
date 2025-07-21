package com.kynsoft.finamer.insis.application.services.batchLog;

import com.kynsoft.finamer.insis.domain.dto.BatchProcessLogDto;
import com.kynsoft.finamer.insis.domain.services.IBatchProcessLogService;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BatchLogService {

    private final IBatchProcessLogService logService;

    public BatchLogService(IBatchProcessLogService logService){
        this.logService = logService;
    }

    @Async
    public BatchProcessLogDto createBatchLog(String hotel,
                                             BatchType batchType,
                                             LocalDate startDate,
                                             LocalDate endDate,
                                             UUID processId){
        BatchProcessLogDto log = new BatchProcessLogDto(
                UUID.randomUUID(),
                batchType,//BatchType.AUTOMATIC,
                BatchStatus.START,
                LocalDateTime.now(),
                null,
                hotel,
                startDate,
                endDate,
                0,
                0,
                processId,
                null
        );
        logService.create(log);

        return log;
    }

    @Async
    public void updateBatchLog(BatchProcessLogDto batchProcessLogDto){
        logService.update(batchProcessLogDto);
    }
}
