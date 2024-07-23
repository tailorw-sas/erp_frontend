package com.kynsoft.finamer.invoicing.infrastructure.excel.event;

import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingRowError;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.BookingImportRowErrorRedisRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ImportBookingRowErrorEventHandler implements ApplicationListener<ImportBookingRowErrorEvent> {

    private final BookingImportRowErrorRedisRepository bookingImportRowErrorRedisRepository;

    public ImportBookingRowErrorEventHandler(BookingImportRowErrorRedisRepository bookingImportRowErrorRedisRepository) {
        this.bookingImportRowErrorRedisRepository = bookingImportRowErrorRedisRepository;
    }

    @Override
    public void onApplicationEvent(ImportBookingRowErrorEvent event) {
        BookingRowError rowError= (BookingRowError) event.getSource();
        bookingImportRowErrorRedisRepository.save(rowError);
    }
}
