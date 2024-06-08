package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageCityState.create.CreateManageCityStateCommand;
import com.kynsoft.finamer.settings.application.command.manageCityState.create.CreateManageCityStateMessage;
import com.kynsoft.finamer.settings.application.command.manageCityState.create.CreateManageCityStateRequest;
import com.kynsoft.finamer.settings.application.command.manageCityState.delete.DeleteManageCityStateCommand;
import com.kynsoft.finamer.settings.application.command.manageCityState.delete.DeleteManageCityStateMessage;
import com.kynsoft.finamer.settings.application.command.manageCityState.update.UpdateManageCityStateCommand;
import com.kynsoft.finamer.settings.application.command.manageCityState.update.UpdateManageCityStateMessage;
import com.kynsoft.finamer.settings.application.command.manageCityState.update.UpdateManageCityStateRequest;
import com.kynsoft.finamer.settings.application.query.manageCityState.getById.FindManageCityStateByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageCityState.search.GetSearchManageCityStateQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageCityStateResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-city-state")
public class ManageCityStateController {

    private final IMediator mediator;

    public ManageCityStateController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManageCityStateMessage> create(@RequestBody CreateManageCityStateRequest request) {
        CreateManageCityStateCommand createCommand = CreateManageCityStateCommand.fromRequest(request);
        CreateManageCityStateMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManageCityStateByIdQuery query = new FindManageCityStateByIdQuery(id);
        ManageCityStateResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManageCityStateCommand command = new DeleteManageCityStateCommand(id);
        DeleteManageCityStateMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageCityStateQuery query = new GetSearchManageCityStateQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageCityStateRequest request) {

        UpdateManageCityStateCommand command = UpdateManageCityStateCommand.fromRequest(request, id);
        UpdateManageCityStateMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
