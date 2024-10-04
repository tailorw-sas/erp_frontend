package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import org.springframework.http.ResponseEntity;

public interface IFormPaymentService {
    ResponseEntity<String> redirectToLink(TransactionDto transactionDto, ManagerMerchantConfigDto merchantConfigDto);
}
