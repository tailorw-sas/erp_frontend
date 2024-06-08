package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;


import com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.create.CreateManageDepartmentGroupCommand;
import com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.create.CreateManageDepartmentGroupMessage;
import com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.create.CreateManageDepartmentGroupRequest;
import com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.delete.DeleteManageDepartmentGroupCommand;
import com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.delete.DeleteManageDepartmentGroupMessage;
import com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.update.UpdateManageDepartmentGroupCommand;
import com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.update.UpdateManageDepartmentGroupMessage;
import com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.update.UpdateManageDepartmentGroupRequest;
import com.kynsoft.finamer.settings.application.query.manageDepartmentGroup.getById.FindManageDepartmentGroupByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageDepartmentGroup.search.GetManageDepartmentGroupQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageDepartmentGroupResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-department-group")
public class ManageDepartmentGroupController {

    private final IMediator mediator;

    public ManageDepartmentGroupController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateManageDepartmentGroupRequest request) {
        CreateManageDepartmentGroupCommand createCommand = CreateManageDepartmentGroupCommand.fromRequest(request);
        CreateManageDepartmentGroupMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManageDepartmentGroupByIdQuery query = new FindManageDepartmentGroupByIdQuery(id);
        ManageDepartmentGroupResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManageDepartmentGroupCommand command = new DeleteManageDepartmentGroupCommand(id);
        DeleteManageDepartmentGroupMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetManageDepartmentGroupQuery query = new GetManageDepartmentGroupQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageDepartmentGroupRequest request) {

        UpdateManageDepartmentGroupCommand command = UpdateManageDepartmentGroupCommand.fromRequest(request, id);
        UpdateManageDepartmentGroupMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
