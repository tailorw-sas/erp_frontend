package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.ProcessErrorLogDto;

public interface IProcessingPendingJobService {

    void checkIsProcessedAndCallTransactionStatus();

    void create(ProcessErrorLogDto dto);

    void update(ProcessErrorLogDto dto);

}
