package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.payment.applyPayment.ApplyPaymentCommand;
import com.kynsoft.finamer.payment.application.command.payment.applyPayment.ApplyPaymentMessage;
import com.kynsoft.finamer.payment.application.command.payment.applyPayment.ApplyPaymentRequest;
import com.kynsoft.finamer.payment.application.command.payment.create.CreatePaymentCommand;
import com.kynsoft.finamer.payment.application.command.payment.create.CreatePaymentMessage;
import com.kynsoft.finamer.payment.application.command.payment.create.CreatePaymentRequest;
import com.kynsoft.finamer.payment.application.command.payment.delete.DeletePaymentCommand;
import com.kynsoft.finamer.payment.application.command.payment.delete.DeletePaymentMessage;
import com.kynsoft.finamer.payment.application.command.payment.update.UpdatePaymentCommand;
import com.kynsoft.finamer.payment.application.command.payment.update.UpdatePaymentMessage;
import com.kynsoft.finamer.payment.application.command.payment.update.UpdatePaymentRequest;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentResponse;
import com.kynsoft.finamer.payment.application.query.payment.getById.FindPaymentByIdQuery;
import com.kynsoft.finamer.payment.application.query.payment.search.GetSearchPaymentQuery;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final IMediator mediator;

    public PaymentController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreatePaymentMessage> create(@RequestBody CreatePaymentRequest request) {
        CreatePaymentCommand createCommand = CreatePaymentCommand.fromRequest(request);
        CreatePaymentMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/apply-payment")
    public ResponseEntity<ApplyPaymentMessage> applyPayment(@RequestBody ApplyPaymentRequest request) {
        ApplyPaymentCommand createCommand = ApplyPaymentCommand.fromRequest(request, mediator);
        ApplyPaymentMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeletePaymentCommand command = new DeletePaymentCommand(id);
        DeletePaymentMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdatePaymentRequest request) {

        UpdatePaymentCommand command = UpdatePaymentCommand.fromRequest(request, id);
        UpdatePaymentMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindPaymentByIdQuery query = new FindPaymentByIdQuery(id);
        PaymentResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchPaymentQuery query = new GetSearchPaymentQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
