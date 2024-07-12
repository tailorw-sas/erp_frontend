package com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking;

import com.kynsoft.finamer.invoicing.domain.excel.validators.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import org.springframework.context.ApplicationEventPublisher;

public class ImportBookingHotelValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageHotelService manageHotelService;
    public ImportBookingHotelValidator(ApplicationEventPublisher applicationEventPublisher, IManageHotelService manageHotelService) {
        super(applicationEventPublisher);
        this.manageHotelService = manageHotelService;
    }

    @Override
    public boolean validate(BookingRow obj) {
        if(!manageHotelService.existByCode(obj.getManageHotelCode())){
            sendErrorEvent(obj.getRowNumber(),new ErrorField("Hotel","Hotel not exist"),obj);
            return false;
        }
        return true;
    }
}
