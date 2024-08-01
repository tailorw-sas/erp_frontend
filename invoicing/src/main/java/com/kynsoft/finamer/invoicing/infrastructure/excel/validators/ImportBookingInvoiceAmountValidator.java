package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class ImportBookingInvoiceAmountValidator extends ExcelRuleValidator<BookingRow> {

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getInvoiceAmount())){
            errorFieldList.add(new ErrorField("Invoice Amount"," Invoice Amount can't be empty"));
            return false;
        }
        if (obj.getInvoiceAmount() == 0) {
            errorFieldList.add( new ErrorField("Invoice Amount", "Invoice Amount must be greater than 0"));
            return false;
        }
        return true;
    }


}
