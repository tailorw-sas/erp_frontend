package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.replicate.object.CreateReplicateCommand;
import com.kynsoft.finamer.invoicing.application.command.replicate.object.CreateReplicateMessage;
import com.kynsoft.finamer.invoicing.application.command.replicate.object.CreateReplicateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/replicate")
public class ReplicateController {

    private final IMediator mediator;

    public ReplicateController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateReplicateMessage> create(@RequestBody CreateReplicateRequest request) {
        CreateReplicateCommand createCommand = CreateReplicateCommand.fromRequest(request);
        CreateReplicateMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }
}
