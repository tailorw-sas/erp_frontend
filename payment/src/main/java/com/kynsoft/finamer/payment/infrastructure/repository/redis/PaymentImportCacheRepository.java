package com.kynsoft.finamer.payment.infrastructure.repository.redis;

import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentImportCacheRepository extends CrudRepository<PaymentImportCache,String> {

    Page<PaymentImportCache> findAllByImportProcessId(String importProcessId, Pageable pageable);

    List<PaymentImportCache> findAllByImportProcessId(String importProcessId);
}
