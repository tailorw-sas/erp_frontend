package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;


import com.kynsoft.finamer.settings.application.command.manageEmployeeGroup.create.CreateManageEmployeeGroupCommand;
import com.kynsoft.finamer.settings.application.command.manageEmployeeGroup.create.CreateManageEmployeeGroupMessage;
import com.kynsoft.finamer.settings.application.command.manageEmployeeGroup.create.CreateManageEmployeeGroupRequest;
import com.kynsoft.finamer.settings.application.command.manageEmployeeGroup.delete.DeleteManageEmployeeGroupCommand;
import com.kynsoft.finamer.settings.application.command.manageEmployeeGroup.delete.DeleteManageEmployeeGroupMessage;
import com.kynsoft.finamer.settings.application.command.manageEmployeeGroup.update.UpdateManageEmployeeGroupCommand;
import com.kynsoft.finamer.settings.application.command.manageEmployeeGroup.update.UpdateManageEmployeeGroupMessage;
import com.kynsoft.finamer.settings.application.command.manageEmployeeGroup.update.UpdateManageEmployeeGroupRequest;
import com.kynsoft.finamer.settings.application.query.manageEmployeeGroup.getById.FindManageEmployeeGroupByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageEmployeeGroup.search.GetManageEmployeeGroupQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageEmployeeGroupResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-employee-group")
public class ManageEmployeeGroupController {

    private final IMediator mediator;

    public ManageEmployeeGroupController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateManageEmployeeGroupRequest request) {
        CreateManageEmployeeGroupCommand createCommand = CreateManageEmployeeGroupCommand.fromRequest(request);
        CreateManageEmployeeGroupMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManageEmployeeGroupByIdQuery query = new FindManageEmployeeGroupByIdQuery(id);
        ManageEmployeeGroupResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManageEmployeeGroupCommand command = new DeleteManageEmployeeGroupCommand(id);
        DeleteManageEmployeeGroupMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetManageEmployeeGroupQuery query = new GetManageEmployeeGroupQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageEmployeeGroupRequest request) {

        UpdateManageEmployeeGroupCommand command = UpdateManageEmployeeGroupCommand.fromRequest(request, id);
        UpdateManageEmployeeGroupMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
