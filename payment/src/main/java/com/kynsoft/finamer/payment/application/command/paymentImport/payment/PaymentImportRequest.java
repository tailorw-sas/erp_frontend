package com.kynsoft.finamer.payment.application.command.paymentImport.payment;

import com.kynsoft.finamer.payment.domain.dtoEnum.EImportPaymentType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentImportRequest {
    private String importProcessId;
    private byte[] file;
    private EImportPaymentType importPaymentType;
}
