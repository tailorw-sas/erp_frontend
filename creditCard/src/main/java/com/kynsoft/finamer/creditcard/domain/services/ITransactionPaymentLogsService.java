package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.TransactionPaymentLogsDto;

import java.util.UUID;

public interface ITransactionPaymentLogsService {
    void update(TransactionPaymentLogsDto dto);
    TransactionPaymentLogsDto findByTransactionId(UUID id);

}
