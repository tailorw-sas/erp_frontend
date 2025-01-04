package com.kynsoft.finamer.payment.application.command.shareFile.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentShareFileDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.attachmentType.AttachmentTypeAntiToIncomeImportMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.rules.attachmentType.AttachmentTypeDefaultMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentShareFileService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdatePaymentShareFileCommandHandler implements ICommandHandler<UpdatePaymentShareFileCommand> {

    private final IPaymentShareFileService service;

    public UpdatePaymentShareFileCommandHandler(IPaymentShareFileService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdatePaymentShareFileCommand command) {

        PaymentShareFileDto paymentShareFileDto = service.findById(command.getId());
        paymentShareFileDto.setFileName(command.getFileName());
        paymentShareFileDto.setFileUrl(command.getFileUrl());
        service.update(paymentShareFileDto);
    }
}
