package com.kynsoft.finamer.payment.domain.excel;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@AllArgsConstructor
@Getter
@RedisHash(value = "paymenterror",timeToLive = 3600)
public class PaymentImportError {

    @Id
    private String id;
    @Indexed
    private String importProcessId;
    private List<ErrorField> errorFields;
    private Object paymentRow;
}
