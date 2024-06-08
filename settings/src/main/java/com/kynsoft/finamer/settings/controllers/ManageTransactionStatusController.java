package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.managerTransactionStatus.create.CreateManageTransactionStatusCommand;
import com.kynsoft.finamer.settings.application.command.managerTransactionStatus.create.CreateManageTransactionStatusMessage;
import com.kynsoft.finamer.settings.application.command.managerTransactionStatus.create.CreateManageTransactionStatusRequest;
import com.kynsoft.finamer.settings.application.command.managerTransactionStatus.delete.DeleteManageTransactionStatusCommand;
import com.kynsoft.finamer.settings.application.command.managerTransactionStatus.delete.DeleteManageTransactionStatusMessage;
import com.kynsoft.finamer.settings.application.command.managerTransactionStatus.update.UpdateManageTransactionStatusCommand;
import com.kynsoft.finamer.settings.application.command.managerTransactionStatus.update.UpdateManageTransactionStatusMessage;
import com.kynsoft.finamer.settings.application.command.managerTransactionStatus.update.UpdateManageTransactionStatusRequest;
import com.kynsoft.finamer.settings.application.query.manageTransactionStatus.getById.FindManageTransactionStatusByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageTransactionStatus.search.GetManageTransactionStatusQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageTransactionStatusResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-transaction-status")
public class ManageTransactionStatusController {

    private final IMediator mediator;

    public ManageTransactionStatusController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManageTransactionStatusMessage> create(@RequestBody CreateManageTransactionStatusRequest request) {
        CreateManageTransactionStatusCommand createCommand = CreateManageTransactionStatusCommand.fromRequest(request);
        CreateManageTransactionStatusMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManageTransactionStatusByIdQuery query = new FindManageTransactionStatusByIdQuery(id);
        ManageTransactionStatusResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManageTransactionStatusCommand command = new DeleteManageTransactionStatusCommand(id);
        DeleteManageTransactionStatusMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetManageTransactionStatusQuery query = new GetManageTransactionStatusQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageTransactionStatusRequest request) {

        UpdateManageTransactionStatusCommand command = UpdateManageTransactionStatusCommand.fromRequest(request, id);
        UpdateManageTransactionStatusMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
