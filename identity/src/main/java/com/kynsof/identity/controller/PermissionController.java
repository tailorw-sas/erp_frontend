package com.kynsof.identity.controller;

import com.kynsof.identity.application.command.permission.create.CreatePermissionCommand;
import com.kynsof.identity.application.command.permission.create.CreatePermissionMessage;
import com.kynsof.identity.application.command.permission.create.CreatePermissionRequest;
import com.kynsof.identity.application.command.permission.delete.DeletePermissionCommand;
import com.kynsof.identity.application.command.permission.delete.DeletePermissionMessage;
import com.kynsof.identity.application.command.permission.deleteAll.DeleteAllPermissionCommand;
import com.kynsof.identity.application.command.permission.deleteAll.DeleteAllPermissionMessage;
import com.kynsof.identity.application.command.permission.deleteAll.DeleteAllPermissionsRequest;
import com.kynsof.identity.application.command.permission.update.UpdatePermissionCommand;
import com.kynsof.identity.application.command.permission.update.UpdatePermissionMessage;
import com.kynsof.identity.application.command.permission.update.UpdatePermissionRequest;
import com.kynsof.identity.application.query.permission.getById.FindPermissionByIdQuery;
import com.kynsof.identity.application.query.permission.getById.PermissionResponse;
import com.kynsof.identity.application.query.permission.search.GetSearchPermissionQuery;
import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    private final IMediator mediator;

    public PermissionController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreatePermissionRequest request) {
        CreatePermissionCommand createCommand = CreatePermissionCommand.fromRequest(request);
        CreatePermissionMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdatePermissionRequest request) {

        UpdatePermissionCommand command = UpdatePermissionCommand.fromRequest(request, id);
        UpdatePermissionMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindPermissionByIdQuery query = new FindPermissionByIdQuery(id);
        PermissionResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchPermissionQuery query = new GetSearchPermissionQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {

        DeletePermissionCommand command = new DeletePermissionCommand(id);
        DeletePermissionMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<?> delete(@RequestBody DeleteAllPermissionsRequest request) {

        DeleteAllPermissionCommand command = new DeleteAllPermissionCommand(request.getPermissions());
        DeleteAllPermissionMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

}
