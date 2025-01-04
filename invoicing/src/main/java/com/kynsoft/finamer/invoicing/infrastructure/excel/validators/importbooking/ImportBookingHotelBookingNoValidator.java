package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportBookingHotelBookingNoValidator extends ExcelRuleValidator<BookingRow> {

    private final String RESERVATION_NUMBER_REGEX="^(I|G)(\\s)+(\\d)+(\\s)+(\\d)+";
    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
      if (Objects.isNull(obj.getHotelBookingNumber()) || obj.getHotelBookingNumber().isEmpty()){
          errorFieldList.add(new ErrorField("Hotel Booking No"," Hotel Booking No. must be not empty"));
          return false;
        }
      if (!Pattern.compile(RESERVATION_NUMBER_REGEX).matcher(obj.getHotelBookingNumber()).matches()){
          errorFieldList.add(new ErrorField("Hotel Booking No"," Hotel Booking No. has invalid format"));
          return false;
      }
      return true;
    }

}
