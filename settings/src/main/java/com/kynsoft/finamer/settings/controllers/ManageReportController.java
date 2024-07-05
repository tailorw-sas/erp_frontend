package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageReport.create.CreateManageReportCommand;
import com.kynsoft.finamer.settings.application.command.manageReport.create.CreateManageReportMessage;
import com.kynsoft.finamer.settings.application.command.manageReport.create.CreateManageReportRequest;
import com.kynsoft.finamer.settings.application.command.manageReport.delete.DeleteManageReportCommand;
import com.kynsoft.finamer.settings.application.command.manageReport.delete.DeleteManageReportMessage;
import com.kynsoft.finamer.settings.application.command.manageReport.update.UpdateManageReportCommand;
import com.kynsoft.finamer.settings.application.command.manageReport.update.UpdateManageReportMessage;
import com.kynsoft.finamer.settings.application.command.manageReport.update.UpdateManageReportRequest;
import com.kynsoft.finamer.settings.application.query.manageReport.findAllGrouped.FindAllManageReportGroupedQuery;
import com.kynsoft.finamer.settings.application.query.manageReport.getById.FindManageReportByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageReport.search.GetSearchManageReportQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageReportResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageReportGroup.ManageReportGroupedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-report")
public class ManageReportController {

    private final IMediator mediator;

    public ManageReportController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageReportRequest request){
        CreateManageReportCommand command = CreateManageReportCommand.fromRequest(request);
        CreateManageReportMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageReportByIdQuery query = new FindManageReportByIdQuery(id);
        ManageReportResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageReportCommand command = new DeleteManageReportCommand(id);
        DeleteManageReportMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageReportQuery query = new GetSearchManageReportQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageReportRequest request){
        UpdateManageReportCommand command = UpdateManageReportCommand.fromRequest(request, id);
        UpdateManageReportMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-grouped")
    public ResponseEntity<?> getAll() {
        FindAllManageReportGroupedQuery query = new FindAllManageReportGroupedQuery();
        ManageReportGroupedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
}
