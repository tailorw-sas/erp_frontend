package com.kynsoft.finamer.payment.application.command.paymentImport.payment;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportService;
import org.springframework.stereotype.Component;

@Component
public class PaymentImportCommandHandler implements ICommandHandler<PaymentImportCommand> {

   private final IPaymentImportService paymentImportService;

    public PaymentImportCommandHandler(IPaymentImportService paymentImportService) {
        this.paymentImportService = paymentImportService;
    }


    @Override
    public void handle(PaymentImportCommand command) {
       paymentImportService.importPaymentFromFile(command.getPaymentImportRequest());
    }
}
