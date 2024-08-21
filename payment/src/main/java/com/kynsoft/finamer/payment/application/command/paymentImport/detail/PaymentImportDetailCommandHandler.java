package com.kynsoft.finamer.payment.application.command.paymentImport.detail;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportDetailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class PaymentImportDetailCommandHandler implements ICommandHandler<PaymentImportDetailCommand> {

    private final IPaymentImportDetailService paymentImportDetail;

    public PaymentImportDetailCommandHandler(IPaymentImportDetailService paymentImportDetail) {
        this.paymentImportDetail = paymentImportDetail;
    }


    @Override
    public void handle(PaymentImportDetailCommand command) {
        paymentImportDetail.importPaymentFromFile(command.getPaymentImportDetailRequest());

    }
}
