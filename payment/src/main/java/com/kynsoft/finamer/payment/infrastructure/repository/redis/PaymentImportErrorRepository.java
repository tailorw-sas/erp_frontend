package com.kynsoft.finamer.payment.infrastructure.repository.redis;

import com.kynsoft.finamer.payment.domain.excel.PaymentImportError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentImportErrorRepository extends CrudRepository<PaymentImportError, String> {

    Page<PaymentImportError> findAllByImportProcessId(String importProcessId, Pageable pageable);

    boolean existsPaymentImportErrorByImportProcessId(String importProcessId);
}
