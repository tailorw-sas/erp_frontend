package com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.reconcileAutomatic.InvoiceReconcileAutomaticRow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@RedisHash(value = "reconcileautomaticerror",timeToLive = 3600)
public class InvoiceReconcileAutomaticImportErrorEntity {
    @Id
    public String id;
    public int rowNumber;
    @Indexed
    public String importProcessId;
    public List<ErrorField> errorFields;
    public InvoiceReconcileAutomaticRow row;
}
