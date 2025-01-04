package com.kynsoft.finamer.payment.application.command.attachment.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateAttachmentMessage implements ICommandMessage {

    private List<MasterPaymentAttachmentDto> respose;

    public CreateAttachmentMessage(List<MasterPaymentAttachmentDto> respose) {
        this.respose = respose;
    }

}
