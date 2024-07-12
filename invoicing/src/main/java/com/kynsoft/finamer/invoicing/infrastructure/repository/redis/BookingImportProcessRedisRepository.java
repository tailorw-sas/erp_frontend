package com.kynsoft.finamer.invoicing.infrastructure.repository.redis;

import com.kynsoft.finamer.invoicing.infrastructure.identity.excel.ImportProcess;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingImportProcessRedisRepository extends CrudRepository<ImportProcess,String> {

    Optional<ImportProcess> findByImportProcessId(String importProcessId);
}
