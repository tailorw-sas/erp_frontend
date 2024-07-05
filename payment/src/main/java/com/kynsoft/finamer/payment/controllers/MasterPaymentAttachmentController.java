package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.create.CreateMasterPaymentAttachmentCommand;
import com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.create.CreateMasterPaymentAttachmentMessage;
import com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.create.CreateMasterPaymentAttachmentRequest;
import com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.delete.DeleteMasterPaymentAttachmentCommand;
import com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.delete.DeleteMasterPaymentAttachmentMessage;
import com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.update.UpdateMasterPaymentAttachmentCommand;
import com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.update.UpdateMasterPaymentAttachmentMessage;
import com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.update.UpdateMasterPaymentAttachmentRequest;
import com.kynsoft.finamer.payment.application.query.masterPaymentAttachment.getById.FindMasterPaymentAttachmentByIdQuery;
import com.kynsoft.finamer.payment.application.query.masterPaymentAttachment.search.GetSearchMasterPaymentAttachmentQuery;
import com.kynsoft.finamer.payment.application.query.objectResponse.MasterPaymentAttachmentResponse;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/master-payment-attachment")
public class MasterPaymentAttachmentController {

    private final IMediator mediator;

    public MasterPaymentAttachmentController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateMasterPaymentAttachmentMessage> create(@RequestBody CreateMasterPaymentAttachmentRequest request) {
        CreateMasterPaymentAttachmentCommand createCommand = CreateMasterPaymentAttachmentCommand.fromRequest(request);
        CreateMasterPaymentAttachmentMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteMasterPaymentAttachmentCommand command = new DeleteMasterPaymentAttachmentCommand(id);
        DeleteMasterPaymentAttachmentMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateMasterPaymentAttachmentRequest request) {

        UpdateMasterPaymentAttachmentCommand command = UpdateMasterPaymentAttachmentCommand.fromRequest(request, id);
        UpdateMasterPaymentAttachmentMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindMasterPaymentAttachmentByIdQuery query = new FindMasterPaymentAttachmentByIdQuery(id);
        MasterPaymentAttachmentResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchMasterPaymentAttachmentQuery query = new GetSearchMasterPaymentAttachmentQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
