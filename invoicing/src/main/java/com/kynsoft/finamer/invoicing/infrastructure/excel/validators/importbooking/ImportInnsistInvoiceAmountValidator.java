package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;

import java.util.List;

public class ImportInnsistInvoiceAmountValidator extends ExcelRuleValidator<BookingRow> {

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        errorFieldList.add(new ErrorField("Invoice Amount", "Invoice Amount must be greater than 0"));
        return false;
    }
}
