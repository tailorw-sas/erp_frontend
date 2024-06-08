package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageInvoiceType.create.CreateManageInvoiceTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageInvoiceType.create.CreateManageInvoiceTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageInvoiceType.create.CreateManageInvoiceTypeRequest;
import com.kynsoft.finamer.settings.application.command.manageInvoiceType.delete.DeleteManageInvoiceTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageInvoiceType.delete.DeleteManageInvoiceTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageInvoiceType.update.UpdateManageInvoiceTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageInvoiceType.update.UpdateManageInvoiceTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageInvoiceType.update.UpdateManageInvoiceTypeRequest;
import com.kynsoft.finamer.settings.application.query.manageInvoiceType.getById.FindManageInvoiceTypeByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageInvoiceType.search.GetSearchManageInvoiceTypeQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageInvoiceTypeResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/manage-invoice-type")
public class ManageInvoiceTypeController {

    private final IMediator mediator;

    public ManageInvoiceTypeController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageInvoiceTypeRequest request){
        CreateManageInvoiceTypeCommand command = CreateManageInvoiceTypeCommand.fromRequest(request);
        CreateManageInvoiceTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageInvoiceTypeByIdQuery query = new FindManageInvoiceTypeByIdQuery(id);
        ManageInvoiceTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageInvoiceTypeCommand command = new DeleteManageInvoiceTypeCommand(id);
        DeleteManageInvoiceTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageInvoiceTypeQuery query = new GetSearchManageInvoiceTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageInvoiceTypeRequest request){
        UpdateManageInvoiceTypeCommand command = UpdateManageInvoiceTypeCommand.fromRequest(request, id);
        UpdateManageInvoiceTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
