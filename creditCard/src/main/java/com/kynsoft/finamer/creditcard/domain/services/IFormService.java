package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageMerchantResponse;
import com.kynsoft.finamer.creditcard.domain.dto.PaymentRequestDto;
import org.springframework.http.ResponseEntity;

public interface IFormService {

    ResponseEntity<String> redirectToBlueMerchant(ManageMerchantResponse response, PaymentRequestDto requestDto, String tokenService);

    ResponseEntity<String> redirectToCardNetMerchant(ManageMerchantResponse response, PaymentRequestDto requestDto, String tokenService);
}
