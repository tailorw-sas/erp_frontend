package com.kynsoft.finamer.payment.application.command.shareFile.create;

import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentShareFileRequest {

    private UUID id;
    private UUID paymentId;
    private String fileName;
    private String fileUrl;
}
