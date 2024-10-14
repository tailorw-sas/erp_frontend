package com.kynsoft.finamer.creditcard.domain.services;

public interface IProcessingPendingJobService {

    void checkIsProcessedAndCallTransactionStatus();

}
