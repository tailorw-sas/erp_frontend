package com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcileAutomatic.reconcile;

import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportError;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportErrorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface InvoiceReconcileAutomaticImportErrorRedisRepository extends CrudRepository<InvoiceReconcileAutomaticImportErrorEntity,String> {

    Page<InvoiceReconcileAutomaticImportErrorEntity> findAllByImportProcessId(String importProcessId, Pageable pageable);

    boolean existsByImportProcessId(String importProcessId);
}
