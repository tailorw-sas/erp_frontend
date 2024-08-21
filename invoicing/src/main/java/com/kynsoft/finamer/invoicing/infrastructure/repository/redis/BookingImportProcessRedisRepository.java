package com.kynsoft.finamer.invoicing.infrastructure.repository.redis;

import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportProcessRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingImportProcessRedisRepository extends CrudRepository<BookingImportProcessRedisEntity,String> {

    Optional<BookingImportProcessRedisEntity> findByImportProcessId(String importProcessId);
}
