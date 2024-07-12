package com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking;

import com.kynsoft.finamer.invoicing.domain.excel.validators.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageRatePlanService;
import org.springframework.context.ApplicationEventPublisher;

public class ImportBookingRatePlanValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageRatePlanService ratePlanService;

    public ImportBookingRatePlanValidator(ApplicationEventPublisher applicationEventPublisher, IManageRatePlanService ratePlanService) {
        super(applicationEventPublisher);
        this.ratePlanService = ratePlanService;
    }

    @Override
    public boolean validate(BookingRow obj) {
        if (obj.getRatePlan().isEmpty()){
            sendErrorEvent(obj.getRowNumber(),new ErrorField("Rate Plan", "Rate Plan  must be not empty"),obj);
            return false;
        }
        else if (!ratePlanService.existByCode(obj.getRoomType())) {
            sendErrorEvent(obj.getRowNumber(),new ErrorField("Rate Plan", "Rate Plan not exist"),obj);
            return false;
        }
        return true;
    }


}
