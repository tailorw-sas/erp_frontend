package com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@Getter
@AllArgsConstructor
@RedisHash(value = "bookingrowerror",timeToLive = 3600)
public class BookingRowError {
    @Id
    public String id;
    public int rowNumber;
    @Indexed
    public String importProcessId;
    public List<ErrorField> errorFields;
    public BookingRow row;
}
