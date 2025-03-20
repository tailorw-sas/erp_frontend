package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IImportInnsistService {

    Page<BookingImportCache> getBookingsFromId(UUID insistImportProcessId, Pageable pageable);
}
