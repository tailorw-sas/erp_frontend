package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.invoiceStatusHistory.update.UpdateInvoiceStatusHistoryCommand;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentMessage;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.delete.DeleteAttachmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.delete.DeleteAttachmentMessage;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.update.UpdateAttachmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.update.UpdateAttachmentMessage;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.update.UpdateAttachmentRequest;
import com.kynsoft.finamer.invoicing.application.query.manageAttachment.getById.FindAttachmentByIdQuery;
import com.kynsoft.finamer.invoicing.application.query.manageAttachment.search.GetSearchAttachmentQuery;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageAttachmentResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-attachment")
public class AttachmentController {

    private final IMediator mediator;

    public AttachmentController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateAttachmentMessage> create(@RequestBody CreateAttachmentRequest request) {
        CreateAttachmentCommand createCommand = CreateAttachmentCommand.fromRequest(request);
        CreateAttachmentMessage response = mediator.send(createCommand);

        UpdateInvoiceStatusHistoryCommand invoiceCommand = new UpdateInvoiceStatusHistoryCommand(request.getInvoice(), request.getEmployee());

        this.mediator.send(invoiceCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindAttachmentByIdQuery query = new FindAttachmentByIdQuery(id);
        ManageAttachmentResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteAttachmentCommand command = new DeleteAttachmentCommand(id);
        DeleteAttachmentMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchAttachmentQuery query = new GetSearchAttachmentQuery(pageable, request.getFilter(),
                request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateAttachmentRequest request) {

        UpdateAttachmentCommand command = UpdateAttachmentCommand.fromRequest(request, id);
        UpdateAttachmentMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
