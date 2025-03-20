package com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking;

import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingImportCacheRedisRepository extends CrudRepository<BookingImportCache, String>,
        QueryByExampleExecutor<BookingImportCache> {

    List<BookingImportCache> findAllByGenerationTypeAndImportProcessId(String generationType, String importProcessId);

    List<BookingImportCache> findAllByImportProcessId(String importProcessId);

    Page<BookingImportCache> findAllByImportProcessId(String importProcessId, Pageable pageable);

    //Optional<BookingImportCache> findBookingImportCacheByHotelBookingNumberAndImportProcessId(String hotelBookingNumber, String importProcessId);

    List<Optional<BookingImportCache>> findAllBookingImportCacheByHotelBookingNumberAndImportProcessId(String hotelBookingNumber, String importProcessId);

    List<BookingImportCache> findBookingImportCacheByHotelInvoiceNumberAndImportProcessId(String hotelInvoiceNumber, String importProcessId);

    List<BookingImportCache> findBookingImportCacheByHotelBookingNumberAndImportProcessId(String hotelBookingNumber, String importProcessId);
}
