package com.kynsoft.finamer.invoicing.infrastructure.identity.excel;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@RedisHash(value = "bookingrowerror",timeToLive = 60)
public class BookingRowError {
    @Id
    public String id;
    public int rowNumber;
    @Indexed
    public String importProcessId;
    public ErrorField errorField;
    public BookingRow row;
}
