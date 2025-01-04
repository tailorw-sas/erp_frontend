package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.checkDatesInCloseOperation.checkDates.CheckDatesInCloseOperationCommand;
import com.kynsoft.finamer.invoicing.application.command.checkDatesInCloseOperation.checkDates.CheckDatesInCloseOperationMessage;
import com.kynsoft.finamer.invoicing.application.command.checkDatesInCloseOperation.checkDates.CheckDatesInCloseOperationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/check-dates")
public class CheckDatesInCloseOperationController {

    private final IMediator mediator;

    public CheckDatesInCloseOperationController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> check(@RequestBody CheckDatesInCloseOperationRequest request) {
        CheckDatesInCloseOperationCommand command = CheckDatesInCloseOperationCommand.fromRequest(request);
        CheckDatesInCloseOperationMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
