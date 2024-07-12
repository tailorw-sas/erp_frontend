package com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking;

import com.kynsoft.finamer.invoicing.domain.excel.validators.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import org.springframework.context.ApplicationEventPublisher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class ImportBookingCheckInValidator extends ExcelRuleValidator<BookingRow> {
    private final String[] validDateFormat = new String[]{"yyyymmdd", "mm/dd/yyyy"};

    public ImportBookingCheckInValidator(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public boolean validate(BookingRow obj) {
        if (!this.validateDate(obj)) {
            return false;
        }
        if (!this.validateDateRange(obj)) {
            return false;
        }
        return true;
    }

    private boolean validateDate(BookingRow obj) {
        String date = obj.getTransactionDate();
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
            sendErrorEvent(obj.getRowNumber(), new ErrorField("CheckIn", "Invalid date format"),obj);
            return false;
        }
        return true;
    }

    private boolean validateDateRange(BookingRow obj) {
        LocalDate checkIn = LocalDate.parse(obj.getCheckIn());
        LocalDate checkOut = LocalDate.parse(obj.getCheckOut());

        if (checkIn.isAfter(checkOut)) {
            sendErrorEvent(obj.getRowNumber(), new ErrorField("CheckIn", "Greater than CheckOut Date"),obj);
            return false;
        }
        return true;
    }

}
