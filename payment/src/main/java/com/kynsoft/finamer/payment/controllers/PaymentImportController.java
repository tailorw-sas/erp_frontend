package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportCommand;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportRequest;
import com.kynsoft.finamer.payment.application.query.paymentImport.payment.error.PaymentImportSearchErrorQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.payment.status.PaymentImportStatusQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.payment.status.PaymentImportStatusResponse;
import com.kynsoft.finamer.payment.domain.dtoEnum.EImportPaymentType;
import org.aspectj.bridge.IMessage;
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
@RequestMapping("/api/payment")
public class PaymentImportController {

    private final IMediator mediator;

    public PaymentImportController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping(value = "/import",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> importPayment(@RequestPart("file") FilePart filePart,
                                                 @RequestPart("importProcessId") String importProcessId,
                                                 @RequestPart("importType") String eImportPaymentType){
        return DataBufferUtils.join(filePart.content())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    PaymentImportRequest paymentImportRequest = new PaymentImportRequest(importProcessId,bytes,EImportPaymentType.valueOf(eImportPaymentType));
                    PaymentImportCommand paymentImportCommand = new PaymentImportCommand(paymentImportRequest);
                    try {
                       IMessage message = mediator.send(paymentImportCommand);
                       return Mono.just(ResponseEntity.ok(message));
                    }catch (Exception e) {
                        return Mono.error(e);
                    }

                } );
    }
    @GetMapping("/{importProcessId}/import-status")
    public ResponseEntity<PaymentImportStatusResponse> importPaymentStatus(@PathVariable("importProcessId") String importProcessId){
        PaymentImportStatusQuery paymentImportStatusQuery = new PaymentImportStatusQuery(importProcessId);
        return ResponseEntity.ok(mediator.send(paymentImportStatusQuery));
    }

    @PostMapping("/import-search")
    public ResponseEntity<?> importPayment(@RequestBody SearchRequest request){
        PaymentImportSearchErrorQuery command = new PaymentImportSearchErrorQuery(request);
        return ResponseEntity.ok(mediator.send(command));
    }

}
