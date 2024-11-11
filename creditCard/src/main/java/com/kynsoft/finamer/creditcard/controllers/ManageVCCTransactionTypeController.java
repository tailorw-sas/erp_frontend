package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.create.CreateManageVCCTransactionTypeCommand;
import com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.create.CreateManageVCCTransactionTypeMessage;
import com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.create.CreateManageVCCTransactionTypeRequest;
import com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.delete.DeleteManageVCCTransactionTypeCommand;
import com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.delete.DeleteManageVCCTransactionTypeMessage;
import com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.update.UpdateManageVCCTransactionTypeCommand;
import com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.update.UpdateManageVCCTransactionTypeMessage;
import com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.update.UpdateManageVCCTransactionTypeRequest;
import com.kynsoft.finamer.creditcard.application.query.manageVCCTransactionType.getById.FindManageVCCTransactionTypeByIdQuery;
import com.kynsoft.finamer.creditcard.application.query.manageVCCTransactionType.search.GetManageVCCTransactionTypeQuery;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageVCCTransactionTypeResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-vcc-transaction-type")
public class ManageVCCTransactionTypeController {

    private final IMediator mediator;

    public ManageVCCTransactionTypeController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateManageVCCTransactionTypeRequest request) {
        CreateManageVCCTransactionTypeCommand createCommand = CreateManageVCCTransactionTypeCommand.fromRequest(request);
        CreateManageVCCTransactionTypeMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManageVCCTransactionTypeByIdQuery query = new FindManageVCCTransactionTypeByIdQuery(id);
        ManageVCCTransactionTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManageVCCTransactionTypeCommand command = new DeleteManageVCCTransactionTypeCommand(id);
        DeleteManageVCCTransactionTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetManageVCCTransactionTypeQuery query = new GetManageVCCTransactionTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageVCCTransactionTypeRequest request) {

        UpdateManageVCCTransactionTypeCommand command = UpdateManageVCCTransactionTypeCommand.fromRequest(request, id);
        UpdateManageVCCTransactionTypeMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
