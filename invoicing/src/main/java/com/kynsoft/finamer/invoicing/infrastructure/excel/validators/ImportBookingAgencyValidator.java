package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import org.springframework.context.ApplicationEventPublisher;

public class ImportBookingAgencyValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageAgencyService manageAgencyService;
    public ImportBookingAgencyValidator(ApplicationEventPublisher applicationEventPublisher, IManageAgencyService manageAgencyService) {
        super(applicationEventPublisher);
        this.manageAgencyService = manageAgencyService;
    }

    @Override
    public boolean validate(BookingRow obj) {
        if(!manageAgencyService.existByCode(obj.getManageAgencyCode())){
            sendErrorEvent(obj.getRowNumber(),new ErrorField("Agency","Agency not exist"),obj);
            return false;
        }
        return true;
    }
}
