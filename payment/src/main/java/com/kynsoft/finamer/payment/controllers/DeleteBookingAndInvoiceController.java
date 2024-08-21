package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.replicate.delete.objects.DeleteReplicateCommand;
import com.kynsoft.finamer.payment.application.command.replicate.delete.objects.DeleteReplicateMessage;
import com.kynsoft.finamer.payment.application.command.replicate.delete.objects.DeleteReplicateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking-invoice")
public class DeleteBookingAndInvoiceController {

    private final IMediator mediator;

    public DeleteBookingAndInvoiceController(IMediator mediator) {
        this.mediator = mediator;
    }

    @DeleteMapping
    public ResponseEntity<DeleteReplicateMessage> create(@RequestBody DeleteReplicateRequest request) {
        DeleteReplicateCommand createCommand = DeleteReplicateCommand.fromRequest(request);
        DeleteReplicateMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }
}
