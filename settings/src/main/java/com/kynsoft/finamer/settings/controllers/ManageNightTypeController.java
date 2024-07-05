package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageNightType.create.CreateManageNightTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageNightType.create.CreateManageNightTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageNightType.create.CreateManageNightTypeRequest;
import com.kynsoft.finamer.settings.application.command.manageNightType.delete.DeleteManageNightTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageNightType.delete.DeleteManageNightTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageNightType.update.UpdateManageNightTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageNightType.update.UpdateManageNightTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageNightType.update.UpdateManageNightTypeRequest;
import com.kynsoft.finamer.settings.application.query.manageNightType.getById.FindManageNightTypeByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageNightType.search.GetSearchManageNightTypeQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageNightTypeResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-night-type")
public class ManageNightTypeController {

    private final IMediator mediator;

    public ManageNightTypeController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManageNightTypeMessage> create(@RequestBody CreateManageNightTypeRequest request) {
        CreateManageNightTypeCommand createCommand = CreateManageNightTypeCommand.fromRequest(request);
        CreateManageNightTypeMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManageNightTypeByIdQuery query = new FindManageNightTypeByIdQuery(id);
        ManageNightTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManageNightTypeCommand command = new DeleteManageNightTypeCommand(id);
        DeleteManageNightTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageNightTypeQuery query = new GetSearchManageNightTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageNightTypeRequest request) {

        UpdateManageNightTypeCommand command = UpdateManageNightTypeCommand.fromRequest(request, id);
        UpdateManageNightTypeMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
