package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageRatePlanService;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class ImportBookingRatePlanValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageRatePlanService ratePlanService;

    public ImportBookingRatePlanValidator(IManageRatePlanService ratePlanService) {
        this.ratePlanService = ratePlanService;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
       if (Objects.nonNull(obj.getRatePlan())&&!ratePlanService.existByCode(obj.getRatePlan())) {
            errorFieldList.add(new ErrorField("Rate Plan", "Rate Plan not exist"));
            return false;
        }
        return true;
    }


}
