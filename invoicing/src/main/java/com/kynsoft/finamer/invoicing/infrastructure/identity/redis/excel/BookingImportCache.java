package com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel;

import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash(value = "importcache",timeToLive =18000L )
public class BookingImportCache {
    @Id
    private String id;
    @Indexed
    private String importProcessId;
    @Indexed
    private String generationType;

    private BookingRow bookingRow;
}
