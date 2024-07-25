package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

public class ImportBookingAgencyValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageAgencyService manageAgencyService;
    public ImportBookingAgencyValidator( IManageAgencyService manageAgencyService) {

        this.manageAgencyService = manageAgencyService;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        if(!manageAgencyService.existByCode(obj.getManageAgencyCode())){
            errorFieldList.add(new ErrorField("Agency","Agency not exist"));
            return false;
        }
        return true;
    }
}
