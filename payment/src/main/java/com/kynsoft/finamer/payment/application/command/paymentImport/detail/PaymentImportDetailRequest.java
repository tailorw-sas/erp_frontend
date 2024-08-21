package com.kynsoft.finamer.payment.application.command.paymentImport.detail;

import com.kynsoft.finamer.payment.domain.dtoEnum.EImportPaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.codec.multipart.FilePart;

@Data
public class PaymentImportDetailRequest {
    private String importProcessId;
   // private double totalAmount;
    private String employeeId;
    private String invoiceTransactionTypeId;
    private byte [] file;
    private byte [] attachment;
    private String attachmentFileName;
    private String paymentId;
    private EImportPaymentType importPaymentType;
}
