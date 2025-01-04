package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ProcessErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessErrorLogReadDataJPARepository extends JpaRepository<ProcessErrorLog, UUID> {

    Boolean existsByTransactionId(UUID id);
}
