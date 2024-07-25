package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.excel.util.DateUtil;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;

public class ImportBookingNightsValidator extends ExcelRuleValidator<BookingRow> {
    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
      int amountNights= (int) obj.getNights();
      LocalDate checkIn=DateUtil.parseDateToLocalDate(obj.getCheckIn());
      LocalDate checkOut=DateUtil.parseDateToLocalDate(obj.getCheckOut());
      int amountDate = (int) checkIn.datesUntil(checkOut).count();
      if (amountNights > amountDate){
          errorFieldList.add(new ErrorField("Nights","Nights is more greater than the nights between CheckIn and CheckOut  "));
          return false;
      }
      if (amountNights < amountDate){
          errorFieldList.add(new ErrorField("Nights","Nights is more less than the nights between CheckIn and CheckOut  "));
          return false;
      }
      return true;
    }
}
