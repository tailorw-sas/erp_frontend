package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.query.managerMerchant.getById.FindManagerMerchantByIdQuery;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageMerchantResponse;
import com.kynsoft.finamer.creditcard.domain.dto.PaymentRequestDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Method;
import com.kynsoft.finamer.creditcard.domain.services.IFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private IMediator mediator;
    @Autowired
    private IFormService formService;

    @PostMapping("/redirect")
    public ResponseEntity<?> getById(@RequestBody PaymentRequestDto requestDto) {
        FindManagerMerchantByIdQuery query = new FindManagerMerchantByIdQuery(requestDto.getMerchantId());
        ManageMerchantResponse response = mediator.send(query);
        ResponseEntity<String> result = null;
        if (response.getMerchantConfigResponse().getMethod().equals(Method.AZUL.toString())) {
            result = formService.redirectToBlueMerchant(response, requestDto);
        }
        if (response.getMerchantConfigResponse().getMethod().equals(Method.CARDNET.toString())) {
            result = formService.redirectToCardNetMerchant(response, requestDto);
        }
        return ResponseEntity.ok(result);
    }

}
