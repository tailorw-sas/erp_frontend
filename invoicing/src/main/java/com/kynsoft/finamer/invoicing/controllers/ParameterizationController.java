package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.parameterization.create.CreateParameterizationCommand;
import com.kynsoft.finamer.invoicing.application.command.parameterization.create.CreateParameterizationMessage;
import com.kynsoft.finamer.invoicing.application.command.parameterization.create.CreateParameterizationRequest;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ParameterizationResponse;
import com.kynsoft.finamer.invoicing.application.query.parameterization.findActive.FindActiveParameterizationQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parameterization")
public class ParameterizationController {

    private final IMediator mediator;

    public ParameterizationController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateParameterizationRequest request){
        CreateParameterizationCommand command = CreateParameterizationCommand.fromRequest(request);
        CreateParameterizationMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> findActive() {
        FindActiveParameterizationQuery query = new FindActiveParameterizationQuery();
        ParameterizationResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
}
