package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.replicate.objects.CreateReplicateCommand;
import com.kynsoft.finamer.payment.application.command.replicate.objects.CreateReplicateMessage;
import com.kynsoft.finamer.payment.application.command.replicate.objects.CreateReplicateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
