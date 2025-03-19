package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface IFormPaymentService {
    MerchantRedirectResponse redirectToMerchant(TransactionDto transactionDto, ManagerMerchantConfigDto merchantConfigDto, ManageMerchantHotelEnrolleDto merchantHotelEnrolleDto);
    UUID create(TransactionPaymentLogsDto dto);
    public void update(TransactionPaymentLogsDto dto);
    TransactionPaymentLogsDto findByTransactionId(UUID id);
}
