package com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.services.paymentDetail.apply.ApplyPaymentDetailService;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import org.springframework.stereotype.Component;


@Component
public class ApplyPaymentDetailCommandHandler implements ICommandHandler<ApplyPaymentDetailCommand> {

    private final ApplyPaymentDetailService applyPaymentDetailService;

    public ApplyPaymentDetailCommandHandler(ApplyPaymentDetailService applyPaymentDetailService) {
        this.applyPaymentDetailService = applyPaymentDetailService;
    }

    @Override
    public void handle(ApplyPaymentDetailCommand command) {
        PaymentDto payment = applyPaymentDetailService.applyDetail(command);
        command.setPaymentResponse(payment);
    }
}
