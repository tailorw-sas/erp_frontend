package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

public class ImportBookingHotelValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageHotelService manageHotelService;
    public ImportBookingHotelValidator(IManageHotelService manageHotelService) {
        this.manageHotelService = manageHotelService;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        if(!manageHotelService.existByCode(obj.getManageHotelCode())){
            errorFieldList.add(new ErrorField("Hotel"," Hotel not exists"));
            return false;
        }
        return true;
    }
}
