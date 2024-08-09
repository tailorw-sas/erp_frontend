package com.kynsoft.finamer.payment.application.command.paymentImport.detail;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportDetailService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PaymentImportDetailCommandHandler implements ICommandHandler<PaymentImportDetailCommand> {

   private final IPaymentImportDetailService paymentImportDetail;

    public PaymentImportDetailCommandHandler(IPaymentImportDetailService paymentImportDetail) {
        this.paymentImportDetail = paymentImportDetail;
    }


    @Override
    public void handle(PaymentImportDetailCommand command) {
     List<UUID> paymentIds = paymentImportDetail.importPaymentFromFile(command.getPaymentImportDetailRequest());
     command.setPaymentIds(paymentIds);
    }
}
