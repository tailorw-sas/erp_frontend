package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailAttachmentCommand;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailCommand;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailMessage;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.PaymentImportDetailSearchErrorQuery;
import com.kynsoft.finamer.payment.domain.dtoEnum.EImportPaymentType;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment-detail")
public class PaymentDetailImportController {

    private final IMediator mediator;

    public PaymentDetailImportController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> importPaymentAnti(@RequestPart("file") FilePart filePart,
                                                     @RequestPart(value = "attachment", required = false) FilePart attachment,
                                                     @RequestPart(value = "totalAmount", required = false) String totalAmount,
                                                     @RequestPart(value = "transactionType",required = false) String invoiceTransactionType,
                                                     @RequestPart("employee") String employee,
                                                     @RequestPart("importProcessId") String importProcessId,
                                                     @RequestPart("importType") String eImportPaymentType) {

        if (EImportPaymentType.ANTI.name().equals(eImportPaymentType)) {
            return DataBufferUtils.join(filePart.content())
                    .flatMap(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        PaymentImportDetailRequest paymentImportDetailRequest = new PaymentImportDetailRequest();
                        paymentImportDetailRequest.setFile(bytes);
                        paymentImportDetailRequest.setImportProcessId(importProcessId);
                        paymentImportDetailRequest.setImportPaymentType(EImportPaymentType.valueOf(eImportPaymentType));
                        paymentImportDetailRequest.setTotalAmount(Double.parseDouble(totalAmount));
                        paymentImportDetailRequest.setEmployeeId(employee);
                        paymentImportDetailRequest.setInvoiceTransactionTypeId(invoiceTransactionType);
                        paymentImportDetailRequest.setAttachment(attachment);
                        PaymentImportDetailCommand paymentImportCommand = new PaymentImportDetailCommand(paymentImportDetailRequest);
                        try {
                            PaymentImportDetailMessage message = mediator.send(paymentImportCommand);
                            return Mono.just(message);
                        } catch (Exception e) {
                            return Mono.error(e);
                        }
                    })
                    .flatMap(message -> DataBufferUtils.join(attachment.content())
                            .flatMap(dataBuffer -> {
                                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(bytes);
                                DataBufferUtils.release(dataBuffer);
                                PaymentImportDetailAttachmentCommand attachmentCommand =
                                        new PaymentImportDetailAttachmentCommand(bytes, attachment.filename(),
                                                message.getPaymentId(),
                                                UUID.fromString(employee),
                                                String.valueOf(bytes.length));
                                try {
                                    mediator.send(attachmentCommand);
                                    return Mono.just(ResponseEntity.ok(message));
                                } catch (Exception e) {
                                    return Mono.error(e);
                                }
                            }));
        } else {
            return DataBufferUtils.join(filePart.content())
                    .flatMap(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        PaymentImportDetailRequest paymentImportDetailRequest = new PaymentImportDetailRequest();
                        paymentImportDetailRequest.setFile(bytes);
                        paymentImportDetailRequest.setImportProcessId(importProcessId);
                        paymentImportDetailRequest.setImportPaymentType(EImportPaymentType.valueOf(eImportPaymentType));
                        paymentImportDetailRequest.setEmployeeId(employee);
                        paymentImportDetailRequest.setInvoiceTransactionTypeId(invoiceTransactionType);
                        PaymentImportDetailCommand paymentImportCommand = new PaymentImportDetailCommand(paymentImportDetailRequest);
                        try {
                            PaymentImportDetailMessage message = mediator.send(paymentImportCommand);
                            return Mono.just(ResponseEntity.ok(message));
                        } catch (Exception e) {
                            return Mono.error(e);
                        }
                    });
        }
    }

//    @GetMapping("/import-status/{importProcessId}")
//    public ResponseEntity<PaymentImportStatusResponse> importPaymentStatus(@PathVariable("importProcessId") String importProcessId) {
//        PaymentImportStatusQuery paymentImportStatusQuery = new PaymentImportStatusQuery(importProcessId);
//        return ResponseEntity.ok(mediator.send(paymentImportStatusQuery));
//    }

    @PostMapping("/import-search")
    public ResponseEntity<?> importPayment(@RequestBody SearchRequest request) {
        PaymentImportDetailSearchErrorQuery command = new PaymentImportDetailSearchErrorQuery(request);
        return ResponseEntity.ok(mediator.send(command));
    }
}
