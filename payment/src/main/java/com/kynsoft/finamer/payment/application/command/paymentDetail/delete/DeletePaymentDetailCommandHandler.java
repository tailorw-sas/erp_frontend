package com.kynsoft.finamer.payment.application.command.paymentDetail.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import org.springframework.stereotype.Component;

@Component
public class DeletePaymentDetailCommandHandler implements ICommandHandler<DeletePaymentDetailCommand> {

    private final IPaymentDetailService service;

    public DeletePaymentDetailCommandHandler(IPaymentDetailService service) {
        this.service = service;
    }

    @Override
    public void handle(DeletePaymentDetailCommand command) {
        PaymentDetailDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
