package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.TransactionPaymentLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface TransactionPaymentLogsReadDataJPARepository extends JpaRepository<TransactionPaymentLogs, UUID>,
        JpaSpecificationExecutor<TransactionPaymentLogs> {
    Optional<TransactionPaymentLogs> findByTransactionId(UUID uuid);

}
