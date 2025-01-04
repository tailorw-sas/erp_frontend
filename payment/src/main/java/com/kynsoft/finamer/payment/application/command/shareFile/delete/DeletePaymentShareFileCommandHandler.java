package com.kynsoft.finamer.payment.application.command.shareFile.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentShareFileDto;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentShareFileService;
import org.springframework.stereotype.Component;

@Component
public class DeletePaymentShareFileCommandHandler implements ICommandHandler<DeletePaymentShareFileCommand> {

    private final IPaymentShareFileService service;

    public DeletePaymentShareFileCommandHandler(IPaymentShareFileService service) {
        this.service = service;
    }

    @Override
    public void handle(DeletePaymentShareFileCommand command) {
        PaymentShareFileDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
