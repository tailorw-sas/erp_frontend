package com.kynsoft.finamer.invoicing.infrastructure.repository.redis;

import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingRowError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingImportRowErrorRedisRepository extends CrudRepository<BookingRowError, String>, PagingAndSortingRepository<BookingRowError,String> {
    Page<BookingRowError> findAllByImportProcessId(String importProcessId, Pageable pageable);

    boolean existsByImportProcessId(String importProcessId);
}
