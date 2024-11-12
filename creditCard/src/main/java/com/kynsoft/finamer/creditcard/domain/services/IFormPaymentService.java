package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.dto.MerchantRedirectResponse;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionPaymentLogsDto;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface IFormPaymentService {
    MerchantRedirectResponse redirectToMerchant(TransactionDto transactionDto, ManagerMerchantConfigDto merchantConfigDto);
    UUID create(TransactionPaymentLogsDto dto);
    public void update(TransactionPaymentLogsDto dto);
    TransactionPaymentLogsDto findByTransactionId(UUID id);
}
