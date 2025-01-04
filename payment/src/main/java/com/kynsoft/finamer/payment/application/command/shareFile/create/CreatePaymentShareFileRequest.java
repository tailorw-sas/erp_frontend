package com.kynsoft.finamer.payment.application.command.shareFile.create;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreatePaymentShareFileRequest {
    private UUID paymentId;
    private String fileName;
    private byte[] file;
}
