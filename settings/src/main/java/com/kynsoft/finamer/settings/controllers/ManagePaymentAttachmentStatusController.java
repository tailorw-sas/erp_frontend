package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.create.CreateManagePaymentAttachmentStatusCommand;
import com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.create.CreateManagePaymentAttachmentStatusMessage;
import com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.create.CreateManagePaymentAttachmentStatusRequest;
import com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.delete.DeleteManagePaymentAttachmentStatusCommand;
import com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.delete.DeleteManagePaymentAttachmentStatusMessage;
import com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.update.UpdateManagePaymentAttachmentStatusCommand;
import com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.update.UpdateManagePaymentAttachmentStatusMessage;
import com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.update.UpdateManagePaymentAttachmentStatusRequest;
import com.kynsoft.finamer.settings.application.query.managePaymentAttachmentStatus.getById.FindPaymentAttachmentStatusByIdQuery;
import com.kynsoft.finamer.settings.application.query.managePaymentAttachmentStatus.search.GetSearchPaymentAttachmentStatusQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagePaymentAttachmentStatusResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-payment-attachment-status")
public class ManagePaymentAttachmentStatusController {

    private final IMediator mediator;

    public ManagePaymentAttachmentStatusController(final IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody final CreateManagePaymentAttachmentStatusRequest request) {
        CreateManagePaymentAttachmentStatusCommand command = CreateManagePaymentAttachmentStatusCommand.fromRequest(request);
        CreateManagePaymentAttachmentStatusMessage message = mediator.send(command);

        return ResponseEntity.ok(message);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindPaymentAttachmentStatusByIdQuery query = new FindPaymentAttachmentStatusByIdQuery(id);
        ManagePaymentAttachmentStatusResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManagePaymentAttachmentStatusCommand command = new DeleteManagePaymentAttachmentStatusCommand(id);
        DeleteManagePaymentAttachmentStatusMessage message = mediator.send(command);

        return ResponseEntity.ok(message);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchPaymentAttachmentStatusQuery query = new GetSearchPaymentAttachmentStatusQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagePaymentAttachmentStatusRequest request) {
        UpdateManagePaymentAttachmentStatusCommand command = UpdateManagePaymentAttachmentStatusCommand.fromRequest(request, id);
        UpdateManagePaymentAttachmentStatusMessage message = mediator.send(command);
        return ResponseEntity.ok(message);
    }
}
