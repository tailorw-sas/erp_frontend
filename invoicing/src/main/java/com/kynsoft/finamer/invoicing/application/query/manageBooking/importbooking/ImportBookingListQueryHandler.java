package com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IImportInnsistService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingRowError;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ImportBookingListQueryHandler implements IQueryHandler<ImportBookingListQuery, PaginatedResponse> {

    private final IImportInnsistService importInnsistService;

    public ImportBookingListQueryHandler(IImportInnsistService importInnsistService){
        this.importInnsistService = importInnsistService;
    }

    @Override
    public PaginatedResponse handle(ImportBookingListQuery query) {
        Page<BookingImportCache> bookingImportCacheList = importInnsistService.getBookingsFromId(query.getImportProcessId(), query.getPageable());

        return new PaginatedResponse(
                bookingImportCacheList.getContent(),
                bookingImportCacheList.getTotalPages(),
                bookingImportCacheList.getNumberOfElements(),
                bookingImportCacheList.getTotalElements(),
                bookingImportCacheList.getSize(),
                bookingImportCacheList.getNumber()
        );
    }
}
