package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageInvoiceStatus.create.CreateManageInvoiceStatusCommand;
import com.kynsoft.finamer.settings.application.command.manageInvoiceStatus.create.CreateManageInvoiceStatusMessage;
import com.kynsoft.finamer.settings.application.command.manageInvoiceStatus.create.CreateManageInvoiceStatusRequest;
import com.kynsoft.finamer.settings.application.command.manageInvoiceStatus.delete.DeleteManageInvoiceStatusCommand;
import com.kynsoft.finamer.settings.application.command.manageInvoiceStatus.delete.DeleteManageInvoiceStatusMessage;
import com.kynsoft.finamer.settings.application.command.manageInvoiceStatus.update.UpdateManageInvoiceStatusCommand;
import com.kynsoft.finamer.settings.application.command.manageInvoiceStatus.update.UpdateManageInvoiceStatusMessage;
import com.kynsoft.finamer.settings.application.command.manageInvoiceStatus.update.UpdateManageInvoiceStatusRequest;
import com.kynsoft.finamer.settings.application.query.manageInvoiceStatus.getById.FindManageInvoiceStatusByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageInvoiceStatus.search.GetSearchManageInvoiceStatusQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageInvoiceStatusResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-invoice-status")
public class ManageInvoiceStatusController {

    private final IMediator mediator;

    public ManageInvoiceStatusController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageInvoiceStatusRequest request){
        CreateManageInvoiceStatusCommand command = CreateManageInvoiceStatusCommand.fromRequest(request);
        CreateManageInvoiceStatusMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageInvoiceStatusByIdQuery query = new FindManageInvoiceStatusByIdQuery(id);
        ManageInvoiceStatusResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageInvoiceStatusCommand command = new DeleteManageInvoiceStatusCommand(id);
        DeleteManageInvoiceStatusMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageInvoiceStatusQuery query = new GetSearchManageInvoiceStatusQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageInvoiceStatusRequest request){
        UpdateManageInvoiceStatusCommand command = UpdateManageInvoiceStatusCommand.fromRequest(request, id);
        UpdateManageInvoiceStatusMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
