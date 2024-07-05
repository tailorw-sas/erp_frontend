package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageAgency.create.CreateManageAgencyCommand;
import com.kynsoft.finamer.settings.application.command.manageAgency.create.CreateManageAgencyMessage;
import com.kynsoft.finamer.settings.application.command.manageAgency.create.CreateManageAgencyRequest;
import com.kynsoft.finamer.settings.application.command.manageAgency.delete.DeleteManageAgencyCommand;
import com.kynsoft.finamer.settings.application.command.manageAgency.delete.DeleteManageAgencyMessage;
import com.kynsoft.finamer.settings.application.command.manageAgency.update.UpdateManageAgencyCommand;
import com.kynsoft.finamer.settings.application.command.manageAgency.update.UpdateManageAgencyMessage;
import com.kynsoft.finamer.settings.application.command.manageAgency.update.UpdateManageAgencyRequest;
import com.kynsoft.finamer.settings.application.query.manageAgency.findAllGrouped.FindAllManageAgencyGroupedQuery;
import com.kynsoft.finamer.settings.application.query.manageAgency.getById.FindManageAgencyByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageAgency.search.GetSearchManageAgencyQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageAgencyResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageAgencyGroup.ManageAgencyGroupedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-agency")
public class ManageAgencyController {

    private final IMediator mediator;

    public ManageAgencyController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageAgencyRequest request){
        CreateManageAgencyCommand command = CreateManageAgencyCommand.fromRequest(request);
        CreateManageAgencyMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageAgencyByIdQuery query = new FindManageAgencyByIdQuery(id);
        ManageAgencyResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageAgencyCommand command = new DeleteManageAgencyCommand(id);
        DeleteManageAgencyMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageAgencyQuery query = new GetSearchManageAgencyQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageAgencyRequest request){
        UpdateManageAgencyCommand command = UpdateManageAgencyCommand.fromRequest(request, id);
        UpdateManageAgencyMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-grouped")
    public ResponseEntity<?> getAll() {
        FindAllManageAgencyGroupedQuery query = new FindAllManageAgencyGroupedQuery();
        ManageAgencyGroupedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
}