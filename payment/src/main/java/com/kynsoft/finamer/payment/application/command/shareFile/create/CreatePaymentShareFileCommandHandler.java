package com.kynsoft.finamer.payment.application.command.shareFile.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentShareFileDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentShareFileService;
import org.springframework.stereotype.Component;

@Component
public class CreatePaymentShareFileCommandHandler implements ICommandHandler<CreatePaymentShareFileCommand> {

    private final IPaymentService paymentService;
    private final IPaymentShareFileService paymentShareFileService;

    public CreatePaymentShareFileCommandHandler(IPaymentService paymentService, IPaymentShareFileService paymentShareFileService) {
        this.paymentService = paymentService;
        this.paymentShareFileService = paymentShareFileService;
    }

    @Override
    public void handle(CreatePaymentShareFileCommand command) {
        PaymentDto paymentDto = this.paymentService.findById(command.getPaymentId());
        paymentShareFileService.create(new PaymentShareFileDto(
                command.getId(),
                 paymentDto,
                command.getFileName(),
                command.getFileUrl()
        ));
    }
}
