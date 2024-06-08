package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.create.CreateManageInvoiceTransactionTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.create.CreateManageInvoiceTransactionTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.create.CreateManageInvoiceTransactionTypeRequest;
import com.kynsoft.finamer.settings.application.command.manageReportParamType.create.CreateManageReportParamTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageReportParamType.create.CreateManageReportParamTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageReportParamType.create.CreateManageReportParamTypeRequest;
import com.kynsoft.finamer.settings.application.command.manageReportParamType.delete.DeleteManageReportParamTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageReportParamType.delete.DeleteManageReportParamTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageReportParamType.update.UpdateManageReportParamTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageReportParamType.update.UpdateManageReportParamTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageReportParamType.update.UpdateManageReportParamTypeRequest;
import com.kynsoft.finamer.settings.application.query.manageReportParamType.getById.FindManageReportParamTypeByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageReportParamType.search.GetSearchManageReportParamTypeQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageReportParamTypeResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-report-param-type")
public class ManageReportParamTypeController {

    private final IMediator mediator;

    public ManageReportParamTypeController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageReportParamTypeRequest request){
        CreateManageReportParamTypeCommand command = CreateManageReportParamTypeCommand.fromRequest(request);
        CreateManageReportParamTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageReportParamTypeByIdQuery query = new FindManageReportParamTypeByIdQuery(id);
        ManageReportParamTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageReportParamTypeCommand command = new DeleteManageReportParamTypeCommand(id);
        DeleteManageReportParamTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageReportParamTypeQuery query = new GetSearchManageReportParamTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageReportParamTypeRequest request){
        UpdateManageReportParamTypeCommand command = UpdateManageReportParamTypeCommand.fromRequest(request, id);
        UpdateManageReportParamTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
