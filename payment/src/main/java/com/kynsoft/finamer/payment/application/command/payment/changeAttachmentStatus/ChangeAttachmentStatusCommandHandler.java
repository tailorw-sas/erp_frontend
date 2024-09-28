package com.kynsoft.finamer.payment.application.command.payment.changeAttachmentStatus;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;

@Component
public class ChangeAttachmentStatusCommandHandler implements ICommandHandler<ChangeAttachmentStatusCommand> {

    private final IPaymentService paymentService;

    public ChangeAttachmentStatusCommandHandler(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void handle(ChangeAttachmentStatusCommand command) {

        PaymentDto paymentDto = this.paymentService.findById(command.getPayment());
        paymentDto.setEAttachment(command.getAttachmentStatus());

        this.paymentService.update(paymentDto);
        command.setPaymentResponse(paymentDto);
    }

}
