package com.kynsoft.finamer.payment.domain.excel;

import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseBookingRow;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@RedisHash(value = "paymentexpensebookingcache",timeToLive = 14400)
public class PaymentExpenseBookingImportCache implements Serializable {
    @Id
    private String id;
    @Indexed
    private String importProcessId;
    private int rowNumber;
    @Indexed
    private String clientName;
    private String bookingId;

    private Double balance;
    private String transactionType;
    private String remarks;


    public PaymentExpenseBookingImportCache(PaymentExpenseBookingRow row){
        this.importProcessId = row.getImportProcessId();
        this.rowNumber = row.getRowNumber();
        this.clientName=row.getClientName();
        this.bookingId = row.getBookingId();
        this.balance = row.getBalance();
        this.transactionType=getTransactionType();
        this.remarks=row.getRemarks();
    }

}
