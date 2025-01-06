package com.kynsoft.finamer.insis.infrastructure.repository.command;

import com.kynsoft.finamer.insis.infrastructure.model.BatchProcessLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BatchProcessLogWriteDataJPARepository extends JpaRepository<BatchProcessLog, UUID> {
}
