package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.CardnetProcessErrorLogDto;

public interface IProcessingPendingJobService {

    void checkIsProcessedAndCallTransactionStatus();

    void create(CardnetProcessErrorLogDto dto);

    void update(CardnetProcessErrorLogDto dto);

}
