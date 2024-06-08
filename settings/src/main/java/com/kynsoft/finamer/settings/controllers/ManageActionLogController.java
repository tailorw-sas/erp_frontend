package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageActionLog.create.CreateManageActionLogCommand;
import com.kynsoft.finamer.settings.application.command.manageActionLog.create.CreateManageActionLogMessage;
import com.kynsoft.finamer.settings.application.command.manageActionLog.create.CreateManageActionLogRequest;
import com.kynsoft.finamer.settings.application.command.manageActionLog.delete.DeleteManageActionLogCommand;
import com.kynsoft.finamer.settings.application.command.manageActionLog.delete.DeleteManageActionLogMessage;
import com.kynsoft.finamer.settings.application.command.manageActionLog.update.UpdateManageActionLogCommand;
import com.kynsoft.finamer.settings.application.command.manageActionLog.update.UpdateManageActionLogMessage;
import com.kynsoft.finamer.settings.application.command.manageActionLog.update.UpdateManageActionLogRequest;
import com.kynsoft.finamer.settings.application.query.manageActionLog.getById.FindManageActionLogByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageActionLog.search.GetSearchManageActionLogQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageActionLogResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-action-log")
public class ManageActionLogController {

    private final IMediator mediator;

    public ManageActionLogController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageActionLogRequest request){
        CreateManageActionLogCommand command = CreateManageActionLogCommand.fromRequest(request);
        CreateManageActionLogMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageActionLogByIdQuery query = new FindManageActionLogByIdQuery(id);
        ManageActionLogResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageActionLogCommand command = new DeleteManageActionLogCommand(id);
        DeleteManageActionLogMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageActionLogQuery query = new GetSearchManageActionLogQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageActionLogRequest request){
        UpdateManageActionLogCommand command = UpdateManageActionLogCommand.fromRequest(request, id);
        UpdateManageActionLogMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
