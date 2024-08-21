package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailCommand;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.error.PaymentImportDetailSearchErrorQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.status.PaymentImportDetailStatusQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.status.PaymentImportDetailStatusResponse;
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

@RestController
@RequestMapping("/api/payment-detail")
public class PaymentDetailImportController {

    private final IMediator mediator;

    public PaymentDetailImportController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importPaymentAnti(@RequestPart("file") FilePart filePart,
                                               @RequestPart(value = "attachment", required = false) FilePart attachment,
                                              // @RequestPart(value = "totalAmount", required = false) String totalAmount,
                                               @RequestPart(value = "transactionType", required = false) String invoiceTransactionType,
                                               @RequestPart("employee") String employee,
                                               @RequestPart("importProcessId") String importProcessId,
                                               @RequestPart(value = "paymentId",required = false) String paymentId,
                                               @RequestPart("importType") String eImportPaymentType) {

        PaymentImportDetailRequest paymentImportDetailRequest = new PaymentImportDetailRequest();
        paymentImportDetailRequest.setImportProcessId(importProcessId);
        paymentImportDetailRequest.setImportPaymentType(EImportPaymentType.valueOf(eImportPaymentType));
        paymentImportDetailRequest.setEmployeeId(employee);
        paymentImportDetailRequest.setInvoiceTransactionTypeId(invoiceTransactionType);

        if (EImportPaymentType.ANTI.name().equals(eImportPaymentType)) {
            DataBufferUtils.join(filePart.content()).flatMap(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);
                paymentImportDetailRequest.setFile(bytes);
                return Mono.just(paymentImportDetailRequest);
            }).subscribe(response->{
                DataBufferUtils.join(attachment.content())
                        .subscribe(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);
                            paymentImportDetailRequest.setAttachment(bytes);
                            //paymentImportDetailRequest.setTotalAmount(Double.parseDouble(totalAmount));
                            paymentImportDetailRequest.setAttachmentFileName(attachment.filename());
                            PaymentImportDetailCommand paymentImportCommand = new PaymentImportDetailCommand(paymentImportDetailRequest);
                            mediator.send(paymentImportCommand);
                        });

            });




        } else {
            DataBufferUtils.join(filePart.content())
                    .flatMap(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);
                        paymentImportDetailRequest.setFile(bytes);
                        PaymentImportDetailCommand paymentImportCommand = new PaymentImportDetailCommand(paymentImportDetailRequest);
                        return Mono.just(ResponseEntity.ok(mediator.send(paymentImportCommand)));
                    }).subscribe();


        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{importProcessId}/import-status")
    public ResponseEntity<PaymentImportDetailStatusResponse> importPaymentStatus(@PathVariable("importProcessId") String importProcessId) {
        PaymentImportDetailStatusQuery paymentImportStatusQuery = new PaymentImportDetailStatusQuery(importProcessId);
        return ResponseEntity.ok(mediator.send(paymentImportStatusQuery));
    }

    @PostMapping("/import-search")
    public ResponseEntity<?> importPayment(@RequestBody SearchRequest request) {
        PaymentImportDetailSearchErrorQuery command = new PaymentImportDetailSearchErrorQuery(request);
        return ResponseEntity.ok(mediator.send(command));
    }
}
