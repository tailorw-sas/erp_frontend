package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.create.CreateManagePaymentTransactionStatusCommand;
import com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.create.CreateManagePaymentTransactionStatusMessage;
import com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.create.CreateManagePaymentTransactionStatusRequest;
import com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.delete.DeleteManagePaymentTransactionStatusCommand;
import com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.delete.DeleteManagePaymentTransactionStatusMessage;
import com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.update.UpdateManagePaymentTransactionStatusCommand;
import com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.update.UpdateManagePaymentTransactionStatusMessage;
import com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.update.UpdateManagePaymentTransactionStatusRequest;
import com.kynsoft.finamer.creditcard.application.query.managePaymentTransactionStatus.getById.FindManagePaymentTransactionStatusByIdQuery;
import com.kynsoft.finamer.creditcard.application.query.managePaymentTransactionStatus.search.GetSearchManagePaymentTransactionStatusQuery;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManagePaymentTransactionStatusResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-payment-transaction-status")
public class ManagePaymentTransactionStatusController {

    private final IMediator mediator;

    public ManagePaymentTransactionStatusController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateManagePaymentTransactionStatusRequest request) {
        CreateManagePaymentTransactionStatusCommand createCommand = CreateManagePaymentTransactionStatusCommand.fromRequest(request);
        CreateManagePaymentTransactionStatusMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManagePaymentTransactionStatusByIdQuery query = new FindManagePaymentTransactionStatusByIdQuery(id);
        ManagePaymentTransactionStatusResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManagePaymentTransactionStatusCommand command = new DeleteManagePaymentTransactionStatusCommand(id);
        DeleteManagePaymentTransactionStatusMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManagePaymentTransactionStatusQuery query = new GetSearchManagePaymentTransactionStatusQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagePaymentTransactionStatusRequest request) {
        UpdateManagePaymentTransactionStatusCommand command = UpdateManagePaymentTransactionStatusCommand.fromRequest(request, id);
        UpdateManagePaymentTransactionStatusMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
