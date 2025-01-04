package com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcileAutomatic.reconcile;

import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportProcessStatusRedisEntity;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportProcessStatusRedisEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InvoiceReconcileAutomaticImportProcessStatusRedisRepository
        extends CrudRepository<InvoiceReconcileAutomaticImportProcessStatusRedisEntity,String> {

    Optional<InvoiceReconcileAutomaticImportProcessStatusRedisEntity> findByImportProcessId(String importProcessId);
}
