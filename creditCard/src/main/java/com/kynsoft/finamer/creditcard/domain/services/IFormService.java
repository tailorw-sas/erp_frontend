package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageMerchantResponse;
import com.kynsoft.finamer.creditcard.domain.dto.CardnetJobDto;
import com.kynsoft.finamer.creditcard.domain.dto.PaymentRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface IFormService {

    ResponseEntity<String> redirectToBlueMerchant(ManageMerchantResponse response, PaymentRequestDto requestDto);
    ResponseEntity<String> redirectToCardNetMerchant(ManageMerchantResponse response, PaymentRequestDto requestDto);
    UUID create(CardnetJobDto dto);
    void update(CardnetJobDto dto);
    CardnetJobDto findByTransactionId(UUID id);
}
