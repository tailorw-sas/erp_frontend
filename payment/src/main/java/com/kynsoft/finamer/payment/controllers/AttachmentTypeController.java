package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.attachmentType.create.CreateAttachmentTypeCommand;
import com.kynsoft.finamer.payment.application.command.attachmentType.create.CreateAttachmentTypeMessage;
import com.kynsoft.finamer.payment.application.command.attachmentType.create.CreateAttachmentTypeRequest;
import com.kynsoft.finamer.payment.application.command.attachmentType.delete.DeleteAttachmentTypeCommand;
import com.kynsoft.finamer.payment.application.command.attachmentType.delete.DeleteAttachmentTypeMessage;
import com.kynsoft.finamer.payment.application.command.attachmentType.update.UpdateAttachmentTypeCommand;
import com.kynsoft.finamer.payment.application.command.attachmentType.update.UpdateAttachmentTypeMessage;
import com.kynsoft.finamer.payment.application.command.attachmentType.update.UpdateAttachmentTypeRequest;
import com.kynsoft.finamer.payment.application.query.attachmentType.getById.FindAttachmentTypeByIdQuery;
import com.kynsoft.finamer.payment.application.query.attachmentType.search.GetSearchAttachmentTypeQuery;
import com.kynsoft.finamer.payment.application.query.objectResponse.AttachmentTypeResponse;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attachment-type")
public class AttachmentTypeController {

    private final IMediator mediator;

    public AttachmentTypeController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateAttachmentTypeMessage> create(@RequestBody CreateAttachmentTypeRequest request) {
        CreateAttachmentTypeCommand createCommand = CreateAttachmentTypeCommand.fromRequest(request);
        CreateAttachmentTypeMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteAttachmentTypeCommand command = new DeleteAttachmentTypeCommand(id);
        DeleteAttachmentTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateAttachmentTypeRequest request) {

        UpdateAttachmentTypeCommand command = UpdateAttachmentTypeCommand.fromRequest(request, id);
        UpdateAttachmentTypeMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindAttachmentTypeByIdQuery query = new FindAttachmentTypeByIdQuery(id);
        AttachmentTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchAttachmentTypeQuery query = new GetSearchAttachmentTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
