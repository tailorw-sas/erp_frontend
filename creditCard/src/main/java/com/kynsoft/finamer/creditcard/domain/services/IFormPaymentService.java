package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionPaymentLogsDto;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface IFormPaymentService {
    ResponseEntity<String> redirectToLink(TransactionDto transactionDto, ManagerMerchantConfigDto merchantConfigDto);
    UUID create(TransactionPaymentLogsDto dto);
}
