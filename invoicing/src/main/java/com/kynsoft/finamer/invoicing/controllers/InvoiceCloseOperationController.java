package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.create.CreateInvoiceCloseOperationCommand;
import com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.create.CreateInvoiceCloseOperationMessage;
import com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.create.CreateInvoiceCloseOperationRequest;
import com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.delete.DeleteInvoiceCloseOperationCommand;
import com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.delete.DeleteInvoiceCloseOperationMessage;
import com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.update.UpdateInvoiceCloseOperationCommand;
import com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.update.UpdateInvoiceCloseOperationMessage;
import com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.update.UpdateInvoiceCloseOperationRequest;
import com.kynsoft.finamer.invoicing.application.query.invoicecloseoperation.getById.FindInvoiceCloseOperationByIdQuery;
import com.kynsoft.finamer.invoicing.application.query.invoicecloseoperation.search.GetSearchInvoiceCloseOperationQuery;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.InvoiceCloseOperationResponse;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice-close-operation")
public class InvoiceCloseOperationController {

    private final IMediator mediator;

    public InvoiceCloseOperationController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateInvoiceCloseOperationMessage> create(@RequestBody CreateInvoiceCloseOperationRequest request) {
        CreateInvoiceCloseOperationCommand createCommand = CreateInvoiceCloseOperationCommand.fromRequest(request);
        CreateInvoiceCloseOperationMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteInvoiceCloseOperationCommand command = new DeleteInvoiceCloseOperationCommand(id);
        DeleteInvoiceCloseOperationMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateInvoiceCloseOperationRequest request) {

        UpdateInvoiceCloseOperationCommand command = UpdateInvoiceCloseOperationCommand.fromRequest(request, id);
        UpdateInvoiceCloseOperationMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindInvoiceCloseOperationByIdQuery query = new FindInvoiceCloseOperationByIdQuery(id);
        InvoiceCloseOperationResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchInvoiceCloseOperationQuery query = new GetSearchInvoiceCloseOperationQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
