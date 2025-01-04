package com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcileAutomatic.reconcile;

import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportCacheEntity;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportErrorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface InvoiceReconcileAutomaticImportCacheRedisRepository extends CrudRepository<InvoiceReconcileAutomaticImportCacheEntity,String> {

    Page<InvoiceReconcileAutomaticImportCacheEntity> findAllByImportProcessId(String importProcessId, Pageable pageable);

    boolean existsByImportProcessId(String importProcessId);
}
