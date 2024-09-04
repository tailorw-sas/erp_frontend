package com.kynsoft.finamer.payment.application.command.attachment.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.application.command.payment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAttachmentCommand implements ICommand {

    private List<CreateAttachmentRequest> attachments;
    private PaymentDto paymentDto;

    private List<MasterPaymentAttachmentDto> dtos;

    public CreateAttachmentCommand(List<CreateAttachmentRequest> attachments, PaymentDto paymentDto) {
        this.attachments = attachments;
        this.paymentDto = paymentDto;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateAttachmentMessage(dtos);
    }
}
