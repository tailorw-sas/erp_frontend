package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageRedirect.CreateRedirectCommandMessage;
import com.kynsoft.finamer.creditcard.application.command.manageRedirect.CreateRedirectCommand;
import com.kynsoft.finamer.creditcard.application.query.managerMerchant.getById.FindManagerMerchantByIdQuery;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageMerchantResponse;
import com.kynsoft.finamer.creditcard.domain.dto.PaymentRequestDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")

public class PaymentController {
    @Autowired
    private IMediator mediator;

    @PostMapping("/redirect")
    public ResponseEntity<?> getForm(@RequestBody PaymentRequestDto requestDto) {
        FindManagerMerchantByIdQuery query = new FindManagerMerchantByIdQuery(requestDto.getMerchantId());
        ManageMerchantResponse response = mediator.send(query);
        CreateRedirectCommand redirectCommand = CreateRedirectCommand.builder()
                .manageMerchantResponse(response)
                .requestDto(requestDto)
                .build();
        CreateRedirectCommandMessage createRedirectCommandMessage = mediator.send(redirectCommand);
        return ResponseEntity.ok(createRedirectCommandMessage);
    }

}
