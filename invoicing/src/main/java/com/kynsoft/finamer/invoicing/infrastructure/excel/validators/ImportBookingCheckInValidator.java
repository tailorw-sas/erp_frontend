package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.excel.util.DateUtil;
import org.springframework.context.ApplicationEventPublisher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ImportBookingCheckInValidator extends ExcelRuleValidator<BookingRow> {
    private final String[] validDateFormat = new String[]{"yyyymmdd", "mm/dd/yyyy"};
    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        if (!this.validateDate(obj,errorFieldList)) {
            return false;
        }
        if (!this.validateDateRange(obj,errorFieldList)) {
            return false;
        }
        return true;
    }

    private boolean validateDate(BookingRow obj,List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getCheckIn()) || obj.getCheckIn().isEmpty()){
            errorFieldList.add(new ErrorField("CheckIn", "CheckIn can't be empty"));
            return false;
        }

        String date = obj.getCheckIn();
        boolean valid = false;
        for (String format : validDateFormat) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            try {
                simpleDateFormat.parse(date);
                valid = true;
                break;
            } catch (ParseException e) {
                valid = false;
            }
        }
        if (!valid) {
           errorFieldList.add(new ErrorField("CheckIn", "CheckIn has invalid date format"));
            return false;
        }
        return true;
    }

    private boolean validateDateRange(BookingRow obj,List<ErrorField> errorFieldList) {
       Optional<LocalDate> checkIn = Optional.ofNullable(DateUtil.parseDateToLocalDate(obj.getCheckIn()));
        Optional<LocalDate> checkOut = Optional.ofNullable(DateUtil.parseDateToLocalDate(obj.getCheckOut()));
        if (checkIn.isPresent() && checkOut.isPresent()) {
            if (checkIn.get().isAfter(checkOut.get())) {
                errorFieldList.add(new ErrorField("CheckIn", "CheckIn is greater than CheckOut Date"));
                return false;
            }
        }else {
            return false;
        }
        return true;
    }

}
