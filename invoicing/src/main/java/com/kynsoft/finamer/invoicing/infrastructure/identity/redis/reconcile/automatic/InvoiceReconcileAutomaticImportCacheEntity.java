package com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@Builder
@RedisHash(value = "invoicereconcileautomaticcache", timeToLive = 18000L)
public class InvoiceReconcileAutomaticImportCacheEntity {
    @Id
    private String id;
    @Indexed
    private String importProcessId;
    private String contract;
    @Indexed
    private Long reservationNumber;
    @Indexed
    private String couponNumber;
    private String nightType;
    private String price;
}
