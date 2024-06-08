package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageClient.create.CreateManageClientCommand;
import com.kynsoft.finamer.settings.application.command.manageClient.create.CreateManageClientMessage;
import com.kynsoft.finamer.settings.application.command.manageClient.create.CreateManageClientRequest;
import com.kynsoft.finamer.settings.application.command.manageClient.delete.DeleteManageClientCommand;
import com.kynsoft.finamer.settings.application.command.manageClient.delete.DeleteManageClientMessage;
import com.kynsoft.finamer.settings.application.command.manageClient.update.UpdateManageClientCommand;
import com.kynsoft.finamer.settings.application.command.manageClient.update.UpdateManageClientMessage;
import com.kynsoft.finamer.settings.application.command.manageClient.update.UpdateManageClientRequest;
import com.kynsoft.finamer.settings.application.query.manageClient.getById.FindManageClientByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageClient.search.GetSearchManageClientQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageClientResponse;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-client")
public class ManageClientController {

    private final IMediator mediator;

    public ManageClientController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManageClientMessage> create(@RequestBody CreateManageClientRequest request) {
        CreateManageClientCommand createCommand = CreateManageClientCommand.fromRequest(request);
        CreateManageClientMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ManageClientResponse> getById(@PathVariable UUID id) {

        FindManageClientByIdQuery query = new FindManageClientByIdQuery(id);
        ManageClientResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<DeleteManageClientMessage> deleteById(@PathVariable UUID id) {

        DeleteManageClientCommand command = new DeleteManageClientCommand(id);
        DeleteManageClientMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageClientQuery query = new GetSearchManageClientQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageClientRequest request) {

        UpdateManageClientCommand command = UpdateManageClientCommand.fromRequest(request, id);
        UpdateManageClientMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
