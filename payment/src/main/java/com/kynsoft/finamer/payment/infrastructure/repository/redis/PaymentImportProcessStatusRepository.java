package com.kynsoft.finamer.payment.infrastructure.repository.redis;

import com.kynsoft.finamer.payment.domain.excel.PaymentImportProcessStatusEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PaymentImportProcessStatusRepository extends CrudRepository<PaymentImportProcessStatusEntity,String> {

    Optional<PaymentImportProcessStatusEntity> findByImportProcessId(String importProcessId);
}
