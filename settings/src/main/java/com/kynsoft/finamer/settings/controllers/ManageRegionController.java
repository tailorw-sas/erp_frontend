package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageRegion.create.CreateManageRegionCommand;
import com.kynsoft.finamer.settings.application.command.manageRegion.create.CreateManageRegionMessage;
import com.kynsoft.finamer.settings.application.command.manageRegion.create.CreateManageRegionRequest;
import com.kynsoft.finamer.settings.application.command.manageRegion.delete.DeleteManageRegionCommand;
import com.kynsoft.finamer.settings.application.command.manageRegion.delete.DeleteManageRegionMessage;
import com.kynsoft.finamer.settings.application.command.manageRegion.update.UpdateManageRegionCommand;
import com.kynsoft.finamer.settings.application.command.manageRegion.update.UpdateManageRegionMessage;
import com.kynsoft.finamer.settings.application.command.manageRegion.update.UpdateManageRegionRequest;
import com.kynsoft.finamer.settings.application.query.manageRegion.getById.FindManageRegionByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageRegion.search.GetSearchManageRegionQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageRegionResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-region")
public class ManageRegionController {

    private final IMediator mediator;

    public ManageRegionController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageRegionRequest request){
        CreateManageRegionCommand command = CreateManageRegionCommand.fromRequest(request);
        CreateManageRegionMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageRegionByIdQuery query = new FindManageRegionByIdQuery(id);
        ManageRegionResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageRegionCommand command = new DeleteManageRegionCommand(id);
        DeleteManageRegionMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageRegionQuery query = new GetSearchManageRegionQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageRegionRequest request){
        UpdateManageRegionCommand command = UpdateManageRegionCommand.fromRequest(request, id);
        UpdateManageRegionMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
