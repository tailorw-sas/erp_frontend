package com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.domain.services.ImportBookingService;
import org.springframework.stereotype.Component;

@Component
public class ImportBookingFromFileErrorQueryHandler implements IQueryHandler<ImportBookingFromFileErrorQuery, ImportBookingFromFileErrorResponse> {

    private final ImportBookingService importBookingService;

    public ImportBookingFromFileErrorQueryHandler(ImportBookingService importBookingService) {
        this.importBookingService = importBookingService;
    }

    @Override
    public ImportBookingFromFileErrorResponse handle(ImportBookingFromFileErrorQuery query) {
        return new ImportBookingFromFileErrorResponse(importBookingService.getImportError(query.getImportBookingErrorRequest()));
    }
}
