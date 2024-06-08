package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.managePaymentStatus.create.CreateManagePaymentStatusCommand;
import com.kynsoft.finamer.settings.application.command.managePaymentStatus.create.CreateManagePaymentStatusMessage;
import com.kynsoft.finamer.settings.application.command.managePaymentStatus.create.CreateManagePaymentStatusRequest;
import com.kynsoft.finamer.settings.application.command.managePaymentStatus.delete.DeletePaymentStatusCommand;
import com.kynsoft.finamer.settings.application.command.managePaymentStatus.delete.DeletePaymentStatusMessage;
import com.kynsoft.finamer.settings.application.command.managePaymentStatus.update.UpdatePaymentStatusCommand;
import com.kynsoft.finamer.settings.application.command.managePaymentStatus.update.UpdatePaymentStatusMessage;
import com.kynsoft.finamer.settings.application.command.managePaymentStatus.update.UpdatePaymentStatusRequest;
import com.kynsoft.finamer.settings.application.query.managePaymentStatus.getById.FindPaymentStatusByIdQuery;
import com.kynsoft.finamer.settings.application.query.managePaymentStatus.search.GetSearchPaymentStatusQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerPaymentStatusResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-payment-status")
public class ManagePaymentStatusController {
    private final IMediator mediator;
    public ManagePaymentStatusController(final IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody final CreateManagePaymentStatusRequest request){
        CreateManagePaymentStatusCommand command = CreateManagePaymentStatusCommand.fromRequest(request);
        CreateManagePaymentStatusMessage message = mediator.send(command);
        
        return ResponseEntity.ok(message);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id){
        FindPaymentStatusByIdQuery query = new FindPaymentStatusByIdQuery(id);
        ManagerPaymentStatusResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeletePaymentStatusCommand command = new DeletePaymentStatusCommand(id);
        DeletePaymentStatusMessage message = mediator.send(command);

        return ResponseEntity.ok(message);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request){
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchPaymentStatusQuery query = new GetSearchPaymentStatusQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdatePaymentStatusRequest request){
        UpdatePaymentStatusCommand command = UpdatePaymentStatusCommand.fromRequest(request, id);
        UpdatePaymentStatusMessage message = mediator.send(command);
        return ResponseEntity.ok(message);
    }
}
