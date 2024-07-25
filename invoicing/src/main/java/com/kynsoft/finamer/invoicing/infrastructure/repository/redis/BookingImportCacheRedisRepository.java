package com.kynsoft.finamer.invoicing.infrastructure.repository.redis;

import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingImportCacheRedisRepository extends CrudRepository<BookingImportCache, String> ,
        QueryByExampleExecutor<BookingImportCache> {
    List<BookingImportCache> findAllByGenerationTypeAndImportProcessId(String generationType, String importProcessId);
    List<BookingImportCache> findAllByImportProcessId(String importProcessId);

    Optional<BookingImportCache> findBookingImportCacheByHotelBookingNumber(String hotelBookingNumber);

}
