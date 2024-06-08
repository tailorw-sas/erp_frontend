package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageAgencyType.create.CreateManageAgencyTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageAgencyType.create.CreateManageAgencyTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageAgencyType.create.CreateManageAgencyTypeRequest;
import com.kynsoft.finamer.settings.application.command.manageAgencyType.delete.DeleteManageAgencyTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageAgencyType.delete.DeleteManageAgencyTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageAgencyType.update.UpdateManageAgencyTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageAgencyType.update.UpdateManageAgencyTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageAgencyType.update.UpdateManageAgencyTypeRequest;
import com.kynsoft.finamer.settings.application.query.manageAgencyType.getById.FindManageAgencyTypeByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageAgencyType.search.GetSearchManageAgencyTypeQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageAgencyTypeResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-agency-type")
public class ManageAgencyTypeController {

    private final IMediator mediator;

    public ManageAgencyTypeController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageAgencyTypeRequest request){
        CreateManageAgencyTypeCommand command = CreateManageAgencyTypeCommand.fromRequest(request);
        CreateManageAgencyTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageAgencyTypeByIdQuery query = new FindManageAgencyTypeByIdQuery(id);
        ManageAgencyTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageAgencyTypeCommand command = new DeleteManageAgencyTypeCommand(id);
        DeleteManageAgencyTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageAgencyTypeQuery query = new GetSearchManageAgencyTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageAgencyTypeRequest request){
        UpdateManageAgencyTypeCommand command = UpdateManageAgencyTypeCommand.fromRequest(request, id);
        UpdateManageAgencyTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
