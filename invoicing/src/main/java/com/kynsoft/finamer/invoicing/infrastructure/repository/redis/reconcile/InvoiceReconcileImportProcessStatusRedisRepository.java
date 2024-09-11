package com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcile;

import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportProcessRedisEntity;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportError;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportProcessStatusRedisEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InvoiceReconcileImportProcessStatusRedisRepository
        extends CrudRepository<InvoiceReconcileImportProcessStatusRedisEntity,String> {

    Optional<InvoiceReconcileImportProcessStatusRedisEntity> findByImportProcessId(String importProcessId);
}
