package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.paymentcloseoperation.create.CreatePaymentCloseOperationCommand;
import com.kynsoft.finamer.payment.application.command.paymentcloseoperation.create.CreatePaymentCloseOperationMessage;
import com.kynsoft.finamer.payment.application.command.paymentcloseoperation.create.CreatePaymentCloseOperationRequest;
import com.kynsoft.finamer.payment.application.command.paymentcloseoperation.delete.DeletePaymentCloseOperationCommand;
import com.kynsoft.finamer.payment.application.command.paymentcloseoperation.delete.DeletePaymentCloseOperationMessage;
import com.kynsoft.finamer.payment.application.command.paymentcloseoperation.update.UpdatePaymentCloseOperationCommand;
import com.kynsoft.finamer.payment.application.command.paymentcloseoperation.update.UpdatePaymentCloseOperationMessage;
import com.kynsoft.finamer.payment.application.command.paymentcloseoperation.update.UpdatePaymentCloseOperationRequest;
import com.kynsoft.finamer.payment.application.command.paymentcloseoperation.updateAll.UpdateAllPaymentCloseOperationCommand;
import com.kynsoft.finamer.payment.application.command.paymentcloseoperation.updateAll.UpdateAllPaymentCloseOperationMessage;
import com.kynsoft.finamer.payment.application.command.paymentcloseoperation.updateAll.UpdateAllPaymentCloseOperationRequest;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentCloseOperationResponse;
import com.kynsoft.finamer.payment.application.query.paymentcloseoperation.getById.FindPaymentCloseOperationByIdQuery;
import com.kynsoft.finamer.payment.application.query.paymentcloseoperation.search.GetSearchPaymentCloseOperationQuery;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment-close-operation")
public class PaymentCloseOperationController {

    private final IMediator mediator;

    public PaymentCloseOperationController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreatePaymentCloseOperationMessage> create(@RequestBody CreatePaymentCloseOperationRequest request) {
        CreatePaymentCloseOperationCommand createCommand = CreatePaymentCloseOperationCommand.fromRequest(request);
        CreatePaymentCloseOperationMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeletePaymentCloseOperationCommand command = new DeletePaymentCloseOperationCommand(id);
        DeletePaymentCloseOperationMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdatePaymentCloseOperationRequest request) {

        UpdatePaymentCloseOperationCommand command = UpdatePaymentCloseOperationCommand.fromRequest(request, id);
        UpdatePaymentCloseOperationMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/all")
    public ResponseEntity<?> update(@RequestBody UpdateAllPaymentCloseOperationRequest request) {

        UpdateAllPaymentCloseOperationCommand command = UpdateAllPaymentCloseOperationCommand.fromRequest(request);
        UpdateAllPaymentCloseOperationMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindPaymentCloseOperationByIdQuery query = new FindPaymentCloseOperationByIdQuery(id);
        PaymentCloseOperationResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchPaymentCloseOperationQuery query = new GetSearchPaymentCloseOperationQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
