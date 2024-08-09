package com.kynsoft.finamer.payment.infrastructure.repository.redis.error;

import com.kynsoft.finamer.payment.domain.excel.error.PaymentBankRowError;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentDetailRowError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentImportDetailErrorRepository extends CrudRepository<PaymentDetailRowError, String> {

    Page<PaymentDetailRowError> findAllByImportProcessId(String importProcessId, Pageable pageable);

    boolean existsPaymentImportErrorByImportProcessId(String importProcessId);
}
