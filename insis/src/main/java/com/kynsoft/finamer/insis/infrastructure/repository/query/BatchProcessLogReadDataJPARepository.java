package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.BatchProcessLog;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchStatus;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface BatchProcessLogReadDataJPARepository extends CrudRepository<BatchProcessLog, UUID>, JpaSpecificationExecutor<BatchProcessLog> {

    //@Query("SELECT COUNT(b) FROM BatchProcessLog b WHERE b.status = :status")
    //Long countByStatus(@Param("status") String status);
    Long countByStatus(BatchStatus status);
}
