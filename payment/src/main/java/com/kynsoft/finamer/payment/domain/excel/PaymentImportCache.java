package com.kynsoft.finamer.payment.domain.excel;

import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentBankRow;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentRow;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@RedisHash(value = "paymentimportcache",timeToLive = 14400)
public class PaymentImportCache implements Serializable {
    @Id
    private String id;
    @Indexed
    private String importProcessId;
    private String importType;
    private int rowNumber;

    private String agency;
    private String hotel;
    private String bankAccount;
    private String paymentAmount;
    private String remarks;
    private String transactionDate;
    private String transactionId;
    private String paymentId;
    private String coupon;
    private String invoiceNo;
    private String anti;


    public PaymentImportCache(PaymentRow paymentRow) {
        this.importProcessId=paymentRow.getImportProcessId();
        this.importType=paymentRow.getImportType();
        this.agency=paymentRow.getManageAgencyCode();
        this.hotel=paymentRow.getManageHotelCode();
        this.paymentAmount= String.valueOf(paymentRow.getAmount());
        this.remarks=paymentRow.getRemarks();
        this.transactionDate = paymentRow.getTransactionDate();
        this.rowNumber=paymentRow.getRowNumber();
    }
    public PaymentImportCache(PaymentBankRow paymentRow) {
        this.importProcessId=paymentRow.getImportProcessId();
        this.importType=paymentRow.getImportType();
        this.agency=paymentRow.getManageAgencyCode();
        this.hotel=paymentRow.getManageHotelCode();
        this.bankAccount= paymentRow.getBankAccount();
        this.remarks=paymentRow.getRemarks();
        this.transactionDate = paymentRow.getTransactionDate();
        this.paymentAmount=String.valueOf(paymentRow.getAmount());
        this.rowNumber=paymentRow.getRowNumber();
    }
    public PaymentImportCache(AntiToIncomeRow paymentRow) {
        this.importProcessId=paymentRow.getImportProcessId();
        this.importType=paymentRow.getImportType();
        this.remarks=paymentRow.getRemarks();
        this.transactionId=paymentRow.getTransactionId();
        this.paymentAmount= String.valueOf(paymentRow.getAmount());
        this.rowNumber=paymentRow.getRowNumber();
    }

    public PaymentImportCache(PaymentDetailRow paymentRow) {
        this.importProcessId=paymentRow.getImportProcessId();
        this.importType=paymentRow.getImportType();
        this.remarks=paymentRow.getRemarks();
        this.paymentId= Objects.nonNull(paymentRow.getPaymentId())? paymentRow.getPaymentId() :"";
        this.paymentAmount= String.valueOf(paymentRow.getBalance());
        this.rowNumber=paymentRow.getRowNumber();
        this.anti=paymentRow.getAnti();
        this.coupon=paymentRow.getCoupon();
        this.invoiceNo= String.valueOf(paymentRow.getInvoiceNo());
        this.transactionId=paymentRow.getTransactionType();
    }

}
