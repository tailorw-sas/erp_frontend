package com.kynsoft.finamer.payment.application.command.payment.changeAttachmentStatus;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentAttachmentStatusService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;

@Component
public class ChangeAttachmentStatusCommandHandler implements ICommandHandler<ChangeAttachmentStatusCommand> {

    private final IPaymentService paymentService;
    private final IManagePaymentAttachmentStatusService paymentAttachmentStatusService;

    public ChangeAttachmentStatusCommandHandler(IPaymentService paymentService, IManagePaymentAttachmentStatusService paymentAttachmentStatusService) {
        this.paymentService = paymentService;
        this.paymentAttachmentStatusService = paymentAttachmentStatusService;
    }

    @Override
    public void handle(ChangeAttachmentStatusCommand command) {

        PaymentDto paymentDto = this.paymentService.findById(command.getPayment());
        switch (command.getAttachmentStatus()) {
            case ATTACHMENT_WITH_ERROR -> {
                paymentDto.setEAttachment(command.getAttachmentStatus());
                paymentDto.setAttachmentStatus(this.paymentAttachmentStatusService.findByPwaWithOutAttachment());
            }
            case ATTACHMENT_WITHOUT_ERROR -> {
                paymentDto.setEAttachment(command.getAttachmentStatus());
                paymentDto.setAttachmentStatus(this.paymentAttachmentStatusService.findByPatWithAttachment());
            }
            case NONE -> {
                paymentDto.setEAttachment(command.getAttachmentStatus());
                paymentDto.setAttachmentStatus(this.paymentAttachmentStatusService.findByNonNone());
            }
            default -> {
            }
        }

        this.paymentService.update(paymentDto);
        command.setPaymentResponse(paymentDto);
    }

}
