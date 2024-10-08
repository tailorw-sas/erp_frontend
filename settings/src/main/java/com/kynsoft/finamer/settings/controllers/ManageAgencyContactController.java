package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageAgencyContact.create.CreateManageAgencyContactCommand;
import com.kynsoft.finamer.settings.application.command.manageAgencyContact.create.CreateManageAgencyContactMessage;
import com.kynsoft.finamer.settings.application.command.manageAgencyContact.create.CreateManageAgencyContactRequest;
import com.kynsoft.finamer.settings.application.command.manageAgencyContact.delete.DeleteManageAgencyContactCommand;
import com.kynsoft.finamer.settings.application.command.manageAgencyContact.delete.DeleteManageAgencyContactMessage;
import com.kynsoft.finamer.settings.application.command.manageAgencyContact.update.UpdateManageAgencyContactCommand;
import com.kynsoft.finamer.settings.application.command.manageAgencyContact.update.UpdateManageAgencyContactMessage;
import com.kynsoft.finamer.settings.application.command.manageAgencyContact.update.UpdateManageAgencyContactRequest;
import com.kynsoft.finamer.settings.application.query.manageAgencyContact.ManageAgencyContactResponse;
import com.kynsoft.finamer.settings.application.query.manageAgencyContact.getById.FindManageAgencyContactByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageAgencyContact.search.GetSearchManageAgencyContactQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-agency-contact")
public class ManageAgencyContactController {

    private final IMediator mediator;

    public ManageAgencyContactController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageAgencyContactRequest request){
        CreateManageAgencyContactCommand command = CreateManageAgencyContactCommand.fromRequest(request);
        CreateManageAgencyContactMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageAgencyContactByIdQuery query = new FindManageAgencyContactByIdQuery(id);
        ManageAgencyContactResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageAgencyContactCommand command = new DeleteManageAgencyContactCommand(id);
        DeleteManageAgencyContactMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageAgencyContactQuery query = new GetSearchManageAgencyContactQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageAgencyContactRequest request){
        UpdateManageAgencyContactCommand command = UpdateManageAgencyContactCommand.fromRequest(request, id);
        UpdateManageAgencyContactMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}