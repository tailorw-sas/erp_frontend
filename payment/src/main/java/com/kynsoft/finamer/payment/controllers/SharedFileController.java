package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportCommand;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportRequest;
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
import com.kynsoft.finamer.payment.application.query.shareFile.search.GetShareFileQuery;
import com.kynsoft.finamer.payment.domain.dtoEnum.EImportPaymentType;
import org.aspectj.bridge.IMessage;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
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

        GetShareFileQuery query = new GetShareFileQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> importPayment(@RequestPart("file") FilePart filePart,
                                                 @RequestPart("paymentId") String paymentId,
                                                 @RequestPart("fileName") String fileName
    ) {
        return DataBufferUtils.join(filePart.content())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    CreatePaymentShareFileRequest request = new CreatePaymentShareFileRequest(UUID.fromString(paymentId), fileName, bytes);
                    CreatePaymentShareFileCommand paymentImportCommand = CreatePaymentShareFileCommand.fromRequest(request);
                    CreatePaymentShareFileMessage message = mediator.send(paymentImportCommand);
                    return Mono.just(ResponseEntity.ok(message));
                });
    }

//    @PostMapping()
//    public ResponseEntity<?> create(@RequestBody CreatePaymentShareFileRequest request) {
//        CreatePaymentShareFileCommand createCommand = CreatePaymentShareFileCommand.fromRequest(request);
//        CreatePaymentShareFileMessage response = mediator.send(createCommand);
//
//        return ResponseEntity.ok(response);
//    }

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
