package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageCollectionStatus.create.CreateManageCollectionStatusCommand;
import com.kynsoft.finamer.settings.application.command.manageCollectionStatus.create.CreateManageCollectionStatusMessage;
import com.kynsoft.finamer.settings.application.command.manageCollectionStatus.create.CreateManageCollectionStatusRequest;
import com.kynsoft.finamer.settings.application.command.manageCollectionStatus.delete.DeleteManageCollectionStatusCommand;
import com.kynsoft.finamer.settings.application.command.manageCollectionStatus.delete.DeleteManageCollectionStatusMessage;
import com.kynsoft.finamer.settings.application.command.manageCollectionStatus.update.UpdateManageCollectionStatusCommand;
import com.kynsoft.finamer.settings.application.command.manageCollectionStatus.update.UpdateManageCollectionStatusMessage;
import com.kynsoft.finamer.settings.application.command.manageCollectionStatus.update.UpdateManageCollectionStatusRequest;
import com.kynsoft.finamer.settings.application.query.manageCollectionStatus.getById.FindManageCollectionStatusByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageCollectionStatus.search.GetSearchManageCollectionStatusQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageCollectionStatusResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-collection-status")
public class ManageCollectionStatusController {

    private final IMediator mediator;

    public ManageCollectionStatusController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageCollectionStatusRequest request){
        CreateManageCollectionStatusCommand command = CreateManageCollectionStatusCommand.fromRequest(request);
        CreateManageCollectionStatusMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageCollectionStatusByIdQuery query = new FindManageCollectionStatusByIdQuery(id);
        ManageCollectionStatusResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageCollectionStatusCommand command = new DeleteManageCollectionStatusCommand(id);
        DeleteManageCollectionStatusMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageCollectionStatusQuery query = new GetSearchManageCollectionStatusQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageCollectionStatusRequest request){
        UpdateManageCollectionStatusCommand command = UpdateManageCollectionStatusCommand.fromRequest(request, id);
        UpdateManageCollectionStatusMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
