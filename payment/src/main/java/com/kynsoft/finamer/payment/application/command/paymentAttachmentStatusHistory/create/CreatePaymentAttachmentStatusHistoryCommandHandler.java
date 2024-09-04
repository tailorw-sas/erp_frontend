package com.kynsoft.finamer.payment.application.command.paymentAttachmentStatusHistory.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;
import org.springframework.stereotype.Component;

@Component
public class CreatePaymentAttachmentStatusHistoryCommandHandler implements ICommandHandler<CreatePaymentAttachmentStatusHistoryCommand> {

    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;

    public CreatePaymentAttachmentStatusHistoryCommandHandler(IPaymentStatusHistoryService paymentAttachmentStatusHistoryService) {
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
    }

    @Override
    public void handle(CreatePaymentAttachmentStatusHistoryCommand command) {

        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(command.getId());
        attachmentStatusHistoryDto.setDescription("Creating Payment.");
        attachmentStatusHistoryDto.setEmployee(command.getEmployeeDto());
        attachmentStatusHistoryDto.setPayment(command.getPayment());
        attachmentStatusHistoryDto.setStatus(command.getPayment().getPaymentStatus().getCode() + "-" + command.getPayment().getPaymentStatus().getName());

        this.paymentAttachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }

}
