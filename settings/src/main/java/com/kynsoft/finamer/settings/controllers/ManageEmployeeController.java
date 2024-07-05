package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageEmployee.clonePermissions.CloneManageEmployeeCommand;
import com.kynsoft.finamer.settings.application.command.manageEmployee.clonePermissions.CloneManageEmployeeMessage;
import com.kynsoft.finamer.settings.application.command.manageEmployee.clonePermissions.CloneManageEmployeeRequest;
import com.kynsoft.finamer.settings.application.command.manageEmployee.create.CreateManageEmployeeCommand;
import com.kynsoft.finamer.settings.application.command.manageEmployee.create.CreateManageEmployeeMessage;
import com.kynsoft.finamer.settings.application.command.manageEmployee.create.CreateManageEmployeeRequest;
import com.kynsoft.finamer.settings.application.command.manageEmployee.delete.DeleteManageEmployeeCommand;
import com.kynsoft.finamer.settings.application.command.manageEmployee.delete.DeleteManageEmployeeMessage;
import com.kynsoft.finamer.settings.application.command.manageEmployee.update.UpdateManageEmployeeCommand;
import com.kynsoft.finamer.settings.application.command.manageEmployee.update.UpdateManageEmployeeMessage;
import com.kynsoft.finamer.settings.application.command.manageEmployee.update.UpdateManageEmployeeRequest;
import com.kynsoft.finamer.settings.application.query.manageEmployee.getById.FindManageEmployeeByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageEmployee.getByIdGrouped.FindManageEmployeeByIdGroupedQuery;
import com.kynsoft.finamer.settings.application.query.manageEmployee.search.GetManageEmployeeQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageEmployee.ManageEmployeeGroupedResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageEmployeeResponse;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-employee")
public class ManageEmployeeController {

    private final IMediator mediator;

    public ManageEmployeeController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManageEmployeeMessage> create(@RequestBody CreateManageEmployeeRequest request) {
        CreateManageEmployeeCommand createCommand = CreateManageEmployeeCommand.fromRequest(request);
        CreateManageEmployeeMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManageEmployeeByIdQuery query = new FindManageEmployeeByIdQuery(id);
        ManageEmployeeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManageEmployeeCommand command = new DeleteManageEmployeeCommand(id);
        DeleteManageEmployeeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetManageEmployeeQuery query = new GetManageEmployeeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageEmployeeRequest request) {

        UpdateManageEmployeeCommand command = UpdateManageEmployeeCommand.fromRequest(request, id);
        UpdateManageEmployeeMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}/grouped")
    public ResponseEntity<?> grouped(@PathVariable UUID id) {

        FindManageEmployeeByIdGroupedQuery query = new FindManageEmployeeByIdGroupedQuery(id);
        ManageEmployeeGroupedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}/clone")
    public ResponseEntity<?> clonePermissions(@PathVariable UUID id, @RequestBody CloneManageEmployeeRequest request) {

        CloneManageEmployeeCommand command = CloneManageEmployeeCommand.fromRequest(request, id);
        CloneManageEmployeeMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
