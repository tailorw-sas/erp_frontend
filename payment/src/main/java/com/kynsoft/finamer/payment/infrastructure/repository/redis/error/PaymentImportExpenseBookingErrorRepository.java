package com.kynsoft.finamer.payment.infrastructure.repository.redis.error;

import com.kynsoft.finamer.payment.domain.excel.error.PaymentExpenseBookingRowError;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentExpenseRowError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentImportExpenseBookingErrorRepository extends CrudRepository<PaymentExpenseBookingRowError, String> {

    Page<PaymentExpenseBookingRowError> findAllByImportProcessId(String importProcessId, Pageable pageable);

    boolean existsPaymentImportErrorByImportProcessId(String importProcessId);
}
