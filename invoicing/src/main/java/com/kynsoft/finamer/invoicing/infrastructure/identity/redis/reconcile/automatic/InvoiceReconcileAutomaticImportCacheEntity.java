package com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@Builder
@RedisHash(value = "invoicereconcileautomaticcache", timeToLive = 18000L)
public class InvoiceReconcileAutomaticImportCacheEntity {
    @Indexed
    private String importProcessId;
    private String contract;
    @Indexed
    private String reservationNumber;
    @Indexed
    private String couponNumber;
    private String nightType;
    private double price;
}
