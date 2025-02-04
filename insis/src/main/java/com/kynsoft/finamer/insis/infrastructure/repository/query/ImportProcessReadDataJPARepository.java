package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.ImportProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ImportProcessReadDataJPARepository extends JpaRepository<ImportProcess, UUID>, JpaSpecificationExecutor<ImportProcess> {
}
