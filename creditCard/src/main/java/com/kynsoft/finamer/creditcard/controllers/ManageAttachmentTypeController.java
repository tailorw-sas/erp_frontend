package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageAttachmentType.create.CreateManageAttachmentTypeCommand;
import com.kynsoft.finamer.creditcard.application.command.manageAttachmentType.create.CreateManageAttachmentTypeMessage;
import com.kynsoft.finamer.creditcard.application.command.manageAttachmentType.create.CreateManageAttachmentTypeRequest;
import com.kynsoft.finamer.creditcard.application.command.manageAttachmentType.delete.DeleteManageAttachmentTypeCommand;
import com.kynsoft.finamer.creditcard.application.command.manageAttachmentType.delete.DeleteManageAttachmentTypeMessage;
import com.kynsoft.finamer.creditcard.application.command.manageAttachmentType.update.UpdateManageAttachmentTypeCommand;
import com.kynsoft.finamer.creditcard.application.command.manageAttachmentType.update.UpdateManageAttachmentTypeMessage;
import com.kynsoft.finamer.creditcard.application.command.manageAttachmentType.update.UpdateManageAttachmentTypeRequest;
import com.kynsoft.finamer.creditcard.application.query.manageAttachmentType.getById.FindManageAttachmentTypeByIdQuery;
import com.kynsoft.finamer.creditcard.application.query.manageAttachmentType.search.GetSearchManageAttachmentQuery;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageAttachmentTypeResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-attachment-type")
public class ManageAttachmentTypeController {

    private final IMediator mediator;

    public ManageAttachmentTypeController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageAttachmentTypeRequest request){
        CreateManageAttachmentTypeCommand command = CreateManageAttachmentTypeCommand.fromRequest(request);
        CreateManageAttachmentTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageAttachmentTypeByIdQuery query = new FindManageAttachmentTypeByIdQuery(id);
        ManageAttachmentTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageAttachmentTypeCommand command = new DeleteManageAttachmentTypeCommand(id);
        DeleteManageAttachmentTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageAttachmentQuery query = new GetSearchManageAttachmentQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageAttachmentTypeRequest request){
        UpdateManageAttachmentTypeCommand command = UpdateManageAttachmentTypeCommand.fromRequest(request, id);
        UpdateManageAttachmentTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
