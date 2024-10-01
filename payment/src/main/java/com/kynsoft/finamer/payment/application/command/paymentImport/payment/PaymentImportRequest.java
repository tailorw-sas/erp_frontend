package com.kynsoft.finamer.payment.application.command.paymentImport.payment;

import com.kynsoft.finamer.payment.domain.dtoEnum.EImportPaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class PaymentImportRequest {
    private String importProcessId;
    private byte[] file;
    private EImportPaymentType importPaymentType;
    private UUID hotelId;
    private UUID employeeId;
}
