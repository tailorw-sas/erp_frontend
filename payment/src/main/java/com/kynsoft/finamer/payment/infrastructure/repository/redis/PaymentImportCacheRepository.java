package com.kynsoft.finamer.payment.infrastructure.repository.redis;

import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface PaymentImportCacheRepository extends CrudRepository<PaymentImportCache,String> {

    Page<PaymentImportCache> findAllByImportProcessId(String importProcessId, Pageable pageable);
}
