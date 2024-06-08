package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;

import com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.create.CreateManageReconcileTransactionStatusCommand;
import com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.create.CreateManageReconcileTransactionStatusMessage;
import com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.create.CreateManageReconcileTransactionStatusRequest;
import com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.delete.DeleteManageReconcileTransactionStatusCommand;
import com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.delete.DeleteManageReconcileTransactionStatusMessage;
import com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.update.UpdateManageReconcileTransactionStatusCommand;
import com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.update.UpdateManageReconcileTransactionStatusMessage;
import com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.update.UpdateManageReconcileTransactionStatusRequest;
import com.kynsoft.finamer.settings.application.query.manageReconcileTransactionStatus.getById.FindManageReconcileTransactionStatusByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageReconcileTransactionStatus.search.GetManageReconcileTransactionStatusQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageReconcileTransactionStatusResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-reconcile-transaction-status")
public class ManageReconcileTransactionStatusController {

    private final IMediator mediator;

    public ManageReconcileTransactionStatusController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateManageReconcileTransactionStatusRequest request) {
        CreateManageReconcileTransactionStatusCommand createCommand = CreateManageReconcileTransactionStatusCommand.fromRequest(request);
        CreateManageReconcileTransactionStatusMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManageReconcileTransactionStatusByIdQuery query = new FindManageReconcileTransactionStatusByIdQuery(id);
        ManageReconcileTransactionStatusResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManageReconcileTransactionStatusCommand command = new DeleteManageReconcileTransactionStatusCommand(id);
        DeleteManageReconcileTransactionStatusMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetManageReconcileTransactionStatusQuery query = new GetManageReconcileTransactionStatusQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageReconcileTransactionStatusRequest request) {

        UpdateManageReconcileTransactionStatusCommand command = UpdateManageReconcileTransactionStatusCommand.fromRequest(request, id);
        UpdateManageReconcileTransactionStatusMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
