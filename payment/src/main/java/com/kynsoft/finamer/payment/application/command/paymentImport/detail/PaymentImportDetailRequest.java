package com.kynsoft.finamer.payment.application.command.paymentImport.detail;

import com.kynsoft.finamer.payment.domain.dtoEnum.EImportPaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class PaymentImportDetailRequest {
    private String importProcessId;
    private int totalAmount;
    private String employeeId;
    private String invoiceTransactionTypeId;
    private byte[] file;
    private EImportPaymentType importPaymentType;
}
