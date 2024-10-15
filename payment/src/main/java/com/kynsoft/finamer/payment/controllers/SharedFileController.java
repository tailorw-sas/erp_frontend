package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.shareFile.create.CreatePaymentShareFileCommand;
import com.kynsoft.finamer.payment.application.command.shareFile.create.CreatePaymentShareFileMessage;
import com.kynsoft.finamer.payment.application.command.shareFile.create.CreatePaymentShareFileRequest;
import com.kynsoft.finamer.payment.application.command.shareFile.delete.DeletePaymentShareFileCommand;
import com.kynsoft.finamer.payment.application.command.shareFile.delete.DeletePaymentShareFileMessage;
import com.kynsoft.finamer.payment.application.command.shareFile.update.UpdatePaymentShareFileCommand;
import com.kynsoft.finamer.payment.application.command.shareFile.update.UpdatePaymentShareFileMessage;
import com.kynsoft.finamer.payment.application.command.shareFile.update.UpdatePaymentShareFileRequest;
import com.kynsoft.finamer.payment.application.query.manageAgency.search.GetSearchAgencyQuery;
import com.kynsoft.finamer.payment.application.query.objectResponse.AttachmentTypeResponse;
import com.kynsoft.finamer.payment.application.query.shareFile.getById.FindPaymentShareFileByIdQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/share-file")
public class SharedFileController {

    private final IMediator mediator;

    public SharedFileController(IMediator mediator) {

        this.mediator = mediator;
    }
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchAgencyQuery query = new GetSearchAgencyQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreatePaymentShareFileRequest request) {
        CreatePaymentShareFileCommand createCommand = CreatePaymentShareFileCommand.fromRequest(request);
        CreatePaymentShareFileMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeletePaymentShareFileCommand command = new DeletePaymentShareFileCommand(id);
        DeletePaymentShareFileMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdatePaymentShareFileRequest request) {

        UpdatePaymentShareFileCommand command = UpdatePaymentShareFileCommand.fromRequest(request, id);
        UpdatePaymentShareFileMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindPaymentShareFileByIdQuery query = new FindPaymentShareFileByIdQuery(id);
        AttachmentTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

}
