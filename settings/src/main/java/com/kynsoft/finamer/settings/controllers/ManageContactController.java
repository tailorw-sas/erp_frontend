package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageContact.create.CreateManageContactCommand;
import com.kynsoft.finamer.settings.application.command.manageContact.create.CreateManageContactMessage;
import com.kynsoft.finamer.settings.application.command.manageContact.create.CreateManageContactRequest;
import com.kynsoft.finamer.settings.application.command.manageContact.delete.DeleteManageContactCommand;
import com.kynsoft.finamer.settings.application.command.manageContact.delete.DeleteManageContactMessage;
import com.kynsoft.finamer.settings.application.command.manageContact.update.UpdateManageContactCommand;
import com.kynsoft.finamer.settings.application.command.manageContact.update.UpdateManageContactMessage;
import com.kynsoft.finamer.settings.application.command.manageContact.update.UpdateManageContactRequest;
import com.kynsoft.finamer.settings.application.query.manageContact.getById.FindManageContactByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageContact.search.GetSearchManageContactQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageContactResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-contact")
public class ManageContactController {

    private final IMediator mediator;

    public ManageContactController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageContactRequest request){
        CreateManageContactCommand command = CreateManageContactCommand.fromRequest(request);
        CreateManageContactMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageContactByIdQuery query = new FindManageContactByIdQuery(id);
        ManageContactResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageContactCommand command = new DeleteManageContactCommand(id);
        DeleteManageContactMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageContactQuery query = new GetSearchManageContactQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageContactRequest request){
        UpdateManageContactCommand command = UpdateManageContactCommand.fromRequest(request, id);
        UpdateManageContactMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
