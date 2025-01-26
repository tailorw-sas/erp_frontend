package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;

import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ImportInsistAdultsValidator extends ExcelRuleValidator<BookingRow> {

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        errorFieldList.add(new ErrorField("Adults", "Adults can't be empty"));
        return false;
    }

}
