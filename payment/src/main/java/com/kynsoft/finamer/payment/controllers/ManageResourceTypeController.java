package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageResourceType.create.CreateManageResourceTypeCommand;
import com.kynsoft.finamer.payment.application.command.manageResourceType.create.CreateManageResourceTypeMessage;
import com.kynsoft.finamer.payment.application.command.manageResourceType.create.CreateManageResourceTypeRequest;
import com.kynsoft.finamer.payment.application.command.manageResourceType.delete.DeleteManageResourceTypeCommand;
import com.kynsoft.finamer.payment.application.command.manageResourceType.delete.DeleteManageResourceTypeMessage;
import com.kynsoft.finamer.payment.application.command.manageResourceType.update.UpdateManageResourceTypeCommand;
import com.kynsoft.finamer.payment.application.command.manageResourceType.update.UpdateManageResourceTypeMessage;
import com.kynsoft.finamer.payment.application.command.manageResourceType.update.UpdateManageResourceTypeRequest;
import com.kynsoft.finamer.payment.application.query.manageResourceType.getById.FindManageResourceTypeByIdQuery;
import com.kynsoft.finamer.payment.application.query.manageResourceType.search.GetSearchManageResourceTypeQuery;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManageResourceTypeResponse;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-resource-type")
public class ManageResourceTypeController {

    private final IMediator mediator;

    public ManageResourceTypeController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManageResourceTypeMessage> create(@RequestBody CreateManageResourceTypeRequest request) {
        CreateManageResourceTypeCommand createCommand = CreateManageResourceTypeCommand.fromRequest(request);
        CreateManageResourceTypeMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManageResourceTypeCommand command = new DeleteManageResourceTypeCommand(id);
        DeleteManageResourceTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageResourceTypeRequest request) {

        UpdateManageResourceTypeCommand command = UpdateManageResourceTypeCommand.fromRequest(request, id);
        UpdateManageResourceTypeMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManageResourceTypeByIdQuery query = new FindManageResourceTypeByIdQuery(id);
        ManageResourceTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageResourceTypeQuery query = new GetSearchManageResourceTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
