package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.excel.util.DateUtil;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.BookingImportCacheRedisRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ImportBookingDateValidator extends ExcelRuleValidator<BookingRow> {

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        return validateDate(obj, errorFieldList) ;
    }

    private boolean validateDate(BookingRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getBookingDate()) || obj.getBookingDate().isEmpty()) {
            errorFieldList.add(new ErrorField("Booking Date", "Booking Date can't be empty"));
            return false;
        }


        if (!DateUtil.validateDateFormat(obj.getBookingDate())) {
            errorFieldList.add(new ErrorField("Booking Date", "Booking Date has invalid date format"));
            return false;
        }

        LocalDate dateToValidate= DateUtil.parseDateToLocalDate(obj.getBookingDate());
        if (dateToValidate.isAfter(LocalDate.now())){
            errorFieldList.add(new ErrorField("Booking Date", "Booking Date can't be future"));
            return false;
        }
        return true;
    }


}
