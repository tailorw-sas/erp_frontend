package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;

import java.io.IOException;

public interface IPdfVoucherService {

    byte[] generatePdf(TransactionDto transactionDto, ManagerMerchantConfigDto merchantConfigDto) throws IOException;
}
