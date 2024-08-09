package com.kynsoft.finamer.invoicing.infrastructure.excel.event.error;

import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingRowError;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.BookingImportRowErrorRedisRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ImportBookingRowErrorEventHandler implements ApplicationListener<ImportBookingRowErrorEvent> {

    private final BookingImportRowErrorRedisRepository bookingImportRowErrorRedisRepository;
    private final ImportBookingRowErrorExtraFields importBookingRowErrorExtraFields;

    public ImportBookingRowErrorEventHandler(BookingImportRowErrorRedisRepository bookingImportRowErrorRedisRepository,
                                             ImportBookingRowErrorExtraFields importBookingRowErrorExtraFields) {
        this.bookingImportRowErrorRedisRepository = bookingImportRowErrorRedisRepository;
        this.importBookingRowErrorExtraFields = importBookingRowErrorExtraFields;
    }

    @Override
    public void onApplicationEvent(ImportBookingRowErrorEvent event) {
        BookingRowError rowError= (BookingRowError) event.getSource();
        rowError=importBookingRowErrorExtraFields.addExtraFieldTrendingCompany(rowError);
        bookingImportRowErrorRedisRepository.save(rowError);
    }
}
