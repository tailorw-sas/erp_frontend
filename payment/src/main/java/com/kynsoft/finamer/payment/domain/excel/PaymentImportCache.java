package com.kynsoft.finamer.payment.domain.excel;

import com.kynsoft.finamer.payment.domain.excel.bean.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.bean.PaymentBankRow;
import com.kynsoft.finamer.payment.domain.excel.bean.PaymentRow;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@AllArgsConstructor
@Getter
@RedisHash(value = "paymentimportcache",timeToLive = 14400)
public class PaymentImportCache {
    @Id
    private String id;
    @Indexed
    private String importProcessId;
    private String importType;

    private String agency;
    private String hotel;
    private String bankAccount;
    private String paymentAmount;
    private String remarks;
    private String transactionDate;
    private String transactionId;

    public PaymentImportCache() {
    }

    public PaymentImportCache(PaymentRow paymentRow) {
        this.importProcessId=paymentRow.getImportProcessId();
        this.importType=paymentRow.getImportType();
        this.agency=paymentRow.getAgencyCode();
        this.hotel=paymentRow.getHotelCode();
        this.paymentAmount= String.valueOf(paymentRow.getPaymentExp());
        this.remarks=paymentRow.getRemarks();
        this.transactionDate = paymentRow.getTransactionDate();
    }
    public PaymentImportCache(PaymentBankRow paymentRow) {
        this.importProcessId=paymentRow.getImportProcessId();
        this.importType=paymentRow.getImportType();
        this.agency=paymentRow.getAgencyCode();
        this.hotel=paymentRow.getHotelCode();
        this.bankAccount= paymentRow.getBankAccount();
        this.remarks=paymentRow.getRemarks();
        this.transactionDate = paymentRow.getTransactionDate();
        this.paymentAmount=String.valueOf(paymentRow.getPaymentAmount());
    }
    public PaymentImportCache(AntiToIncomeRow paymentRow) {
        this.importProcessId=paymentRow.getImportProcessId();
        this.importType=paymentRow.getImportType();
        this.remarks=paymentRow.getRemarks();
        this.transactionId=paymentRow.getTransactionId();
        this.paymentAmount= String.valueOf(paymentRow.getAmount());
    }

}
