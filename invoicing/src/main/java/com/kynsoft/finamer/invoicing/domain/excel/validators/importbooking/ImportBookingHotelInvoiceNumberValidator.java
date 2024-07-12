package com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking;

import com.kynsoft.finamer.invoicing.domain.excel.validators.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import org.springframework.context.ApplicationEventPublisher;

public class ImportBookingHotelInvoiceNumberValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageHotelService manageHotelService;

    public ImportBookingHotelInvoiceNumberValidator(ApplicationEventPublisher applicationEventPublisher, IManageHotelService manageHotelService) {
        super(applicationEventPublisher);
        this.manageHotelService = manageHotelService;
    }

    @Override
    public boolean validate(BookingRow obj) {
        //TODO
        //Esperar a que keymer responda.
        return true;
    }


}
