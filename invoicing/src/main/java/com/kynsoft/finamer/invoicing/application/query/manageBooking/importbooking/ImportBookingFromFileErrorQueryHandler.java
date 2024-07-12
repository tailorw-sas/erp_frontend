package com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.domain.excel.validators.ImportBookingProcessStatusRequest;
import com.kynsoft.finamer.invoicing.domain.services.ImportBookingService;

public class ImportBookingFromFileErrorQueryHandler implements IQueryHandler<ImportBookingFromFileErrorQuery, ImportBookingFormFileErrorResponse> {

    private final ImportBookingService importBookingService;

    public ImportBookingFromFileErrorQueryHandler(ImportBookingService importBookingService) {
        this.importBookingService = importBookingService;
    }

    @Override
    public ImportBookingFormFileErrorResponse handle(ImportBookingFromFileErrorQuery query) {
        return new ImportBookingFormFileErrorResponse(importBookingService.getImportError(query.getImportBookingErrorRequest()));
    }
}
