package com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcile;

import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingRowError;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface InvoiceReconcileImportErrorRedisRepository extends CrudRepository<InvoiceReconcileImportError,String> {

    Page<InvoiceReconcileImportError> findAllByImportProcessId(String importProcessId, Pageable pageable);

    boolean existsByImportProcessId(String importProcessId);
}
