package com.kynsoft.finamer.payment.infrastructure.repository.redis.expenseBooking;

import com.kynsoft.finamer.payment.domain.excel.PaymentExpenseBookingImportCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentExpenseBookingImportCacheRepository extends CrudRepository<PaymentExpenseBookingImportCache,String> {

    Page<PaymentExpenseBookingImportCache> findAllByImportProcessId(String importProcessId, Pageable pageable);
    Page<PaymentExpenseBookingImportCache> findAllByImportProcessIdAndClientName(String importProcessId,String clientName,Pageable pageable);
}
