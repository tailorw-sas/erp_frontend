package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.create.CreateManageInvoiceTransactionTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.create.CreateManageInvoiceTransactionTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.create.CreateManageInvoiceTransactionTypeRequest;
import com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.delete.DeleteManageInvoiceTransactionTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.delete.DeleteManageInvoiceTransactionTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.update.UpdateManageInvoiceTransactionTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.update.UpdateManageInvoiceTransactionTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.update.UpdateManageInvoiceTransactionTypeRequest;
import com.kynsoft.finamer.settings.application.query.manageInvoiceTransactionType.getById.FindManageInvoiceTransactionTypeByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageInvoiceTransactionType.search.GetSearchManageInvoiceTransactionTypeQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageInvoiceTransactionTypeResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-invoice-transaction-type")
public class ManageInvoiceTransactionTypeController {

    private final IMediator mediator;

    public ManageInvoiceTransactionTypeController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageInvoiceTransactionTypeRequest request){
        CreateManageInvoiceTransactionTypeCommand command = CreateManageInvoiceTransactionTypeCommand.fromRequest(request);
        CreateManageInvoiceTransactionTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageInvoiceTransactionTypeByIdQuery query = new FindManageInvoiceTransactionTypeByIdQuery(id);
        ManageInvoiceTransactionTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageInvoiceTransactionTypeCommand command = new DeleteManageInvoiceTransactionTypeCommand(id);
        DeleteManageInvoiceTransactionTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageInvoiceTransactionTypeQuery query = new GetSearchManageInvoiceTransactionTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageInvoiceTransactionTypeRequest request){
        UpdateManageInvoiceTransactionTypeCommand command = UpdateManageInvoiceTransactionTypeCommand.fromRequest(request, id);
        UpdateManageInvoiceTransactionTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
