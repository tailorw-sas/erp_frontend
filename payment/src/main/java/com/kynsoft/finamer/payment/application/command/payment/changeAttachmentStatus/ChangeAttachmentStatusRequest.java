package com.kynsoft.finamer.payment.application.command.payment.changeAttachmentStatus;

import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChangeAttachmentStatusRequest {

    private EAttachment status;
    private UUID payment;
}
