package com.kynsoft.finamer.payment.domain.excel;

import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentBankRow;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseBookingRow;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseRow;
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
public class PaymentExpenseBookingImportCache implements Serializable {
    @Id
    private String id;
    @Indexed
    private String importProcessId;
    private int rowNumber;
    @Indexed
    private String clientName;


    public PaymentExpenseBookingImportCache(PaymentExpenseBookingRow row){
        this.importProcessId = row.getImportProcessId();
        this.rowNumber = row.getRowNumber();
        this.clientName=row.getClientName();
    }

}
