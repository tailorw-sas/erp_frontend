package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;


import com.kynsoft.finamer.settings.application.command.managePermissionModule.create.CreateManagePermissionModuleCommand;
import com.kynsoft.finamer.settings.application.command.managePermissionModule.create.CreateManagePermissionModuleMessage;
import com.kynsoft.finamer.settings.application.command.managePermissionModule.create.CreateManagePermissionModuleRequest;
import com.kynsoft.finamer.settings.application.command.managePermissionModule.delete.DeleteManagePermissionModuleCommand;
import com.kynsoft.finamer.settings.application.command.managePermissionModule.delete.DeleteManagePermissionModuleMessage;
import com.kynsoft.finamer.settings.application.command.managePermissionModule.update.UpdateManagePermissionModuleCommand;
import com.kynsoft.finamer.settings.application.command.managePermissionModule.update.UpdateManagePermissionModuleMessage;
import com.kynsoft.finamer.settings.application.command.managePermissionModule.update.UpdateManagePermissionModuleRequest;
import com.kynsoft.finamer.settings.application.query.managePermissionModule.getById.FindManagePermissionModuleByIdQuery;
import com.kynsoft.finamer.settings.application.query.managePermissionModule.search.GetManagePermissionModuleQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagePermissionModuleResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-permission-module")
public class ManagePermissionModuleController {

    private final IMediator mediator;

    public ManagePermissionModuleController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateManagePermissionModuleRequest request) {
        CreateManagePermissionModuleCommand createCommand = CreateManagePermissionModuleCommand.fromRequest(request);
        CreateManagePermissionModuleMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManagePermissionModuleByIdQuery query = new FindManagePermissionModuleByIdQuery(id);
        ManagePermissionModuleResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManagePermissionModuleCommand command = new DeleteManagePermissionModuleCommand(id);
        DeleteManagePermissionModuleMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetManagePermissionModuleQuery query = new GetManagePermissionModuleQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagePermissionModuleRequest request) {

        UpdateManagePermissionModuleCommand command = UpdateManagePermissionModuleCommand.fromRequest(request, id);
        UpdateManagePermissionModuleMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
