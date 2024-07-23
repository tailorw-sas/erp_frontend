package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.excel.util.DateUtil;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.ImportBookingService;
import org.springframework.context.ApplicationEventPublisher;

public class ImportBookingDuplicateValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageBookingService service;

    public ImportBookingDuplicateValidator(ApplicationEventPublisher applicationEventPublisher, IManageBookingService service) {
        super(applicationEventPublisher);
        this.service = service;
    }

    @Override
    public boolean validate(BookingRow obj) {
        if (service.existByBookingHotelNumber(obj.getHotelBookingNumber())) {
            sendErrorEvent(obj.getRowNumber(), new ErrorField("Hotel Booking Number", "Duplicate Import"), obj);
            return false;
        }
        return true;
    }
}
