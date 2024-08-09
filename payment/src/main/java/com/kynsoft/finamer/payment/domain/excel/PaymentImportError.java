package com.kynsoft.finamer.payment.domain.excel;

import com.kynsof.share.core.domain.response.ErrorField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@RedisHash(value = "paymenterror",timeToLive = 3600)
public class PaymentImportError implements Serializable {

    @Id
    private String id;
    private int rowNumber;
    @Indexed
    private String importProcessId;
    private List<ErrorField> errorFields;
    private Object row;
}
