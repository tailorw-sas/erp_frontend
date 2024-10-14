package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.excel.util.DateUtil;


import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ImportBookingNightsValidator extends ExcelRuleValidator<BookingRow> {
    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getNights())) {
            errorFieldList.add(new ErrorField("Nights", "Nights can't be empty"));
            return false;
        }
        boolean apply =errorFieldList.stream().anyMatch(errorField ->"CheckIn".equals(errorField.getField()) || "CheckOut".equals(errorField.getField()));
        if (!apply) {
            int amountNights = obj.getNights().intValue();
            if (Objects.nonNull(obj.getCheckIn()) && Objects.nonNull(obj.getCheckOut())) {
                LocalDate checkIn = DateUtil.parseDateToLocalDate(obj.getCheckIn());
                LocalDate checkOut = DateUtil.parseDateToLocalDate(obj.getCheckOut());
                if ((Objects.nonNull(checkIn) && Objects.nonNull(checkOut))) {
                    int amountDate = (int) checkIn.datesUntil(checkOut).count();
                    if (amountNights > amountDate) {
                        errorFieldList.add(new ErrorField("Nights", "Nights is more greater than the nights between CheckIn and CheckOut  "));
                        return false;
                    }
                    if (amountNights < amountDate) {
                        errorFieldList.add(new ErrorField("Nights", "Nights is more less than the nights between CheckIn and CheckOut  "));
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }
}
