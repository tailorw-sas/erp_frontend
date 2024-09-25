package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.query.managerMerchant.getById.FindManagerMerchantByIdQuery;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageMerchantResponse;
import com.kynsoft.finamer.creditcard.domain.dto.PaymentRequestDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Method;
import com.kynsoft.finamer.creditcard.domain.services.IFormService;
import com.kynsoft.finamer.creditcard.infrastructure.services.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {
    @Autowired
    private IMediator mediator;
    @Autowired
    private IFormService formService;


    @PostMapping("/redirect")
    public ResponseEntity<?> getById(@RequestBody PaymentRequestDto requestDto) {

        FindManagerMerchantByIdQuery query = new FindManagerMerchantByIdQuery(requestDto.getMerchantId(),requestDto.getTransactionUuid());
        ManageMerchantResponse response = mediator.send(query);
        ResponseEntity<String> result = null;
        String tokenService = String.valueOf(new TokenService().generateToken(requestDto));

        if (response.getMerchantConfigResponse().getMethod().equals(Method.AZUL.toString())) {
            result = formService.redirectToBlueMerchant(response, requestDto, tokenService);
        }
        if (response.getMerchantConfigResponse().getMethod().equals(Method.CARDNET.toString())) {
            result = formService.redirectToCardNetMerchant(response, requestDto, tokenService);
        }
        log.info("Response: {}", response);
        return ResponseEntity.ok(result);

    }

}
