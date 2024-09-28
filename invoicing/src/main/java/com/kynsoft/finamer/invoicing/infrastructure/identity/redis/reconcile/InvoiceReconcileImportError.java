package com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@RedisHash(value = "reconcileerror",timeToLive = 3600)
public class InvoiceReconcileImportError {
    @Id
    public String id;
    @Indexed
    public String importProcessId;
    public String message;
    public String filename;
}
