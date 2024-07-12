package com.kynsoft.finamer.invoicing.infrastructure.repository.redis;

import com.kynsoft.finamer.invoicing.infrastructure.identity.excel.BookingRowError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingImportRowErrorRedisRepository extends CrudRepository<BookingRowError, Integer> {

    Page<BookingRowError> findAllByIAndImportProcessId(String importProcessId, Pageable pageable);
}
