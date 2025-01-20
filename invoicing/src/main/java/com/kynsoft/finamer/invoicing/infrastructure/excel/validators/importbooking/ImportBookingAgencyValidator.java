package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;

import java.util.List;
import java.util.Objects;

public class ImportBookingAgencyValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageAgencyService manageAgencyService;

    public ImportBookingAgencyValidator(IManageAgencyService manageAgencyService) {

        this.manageAgencyService = manageAgencyService;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {

        if (Objects.isNull(obj.getManageAgencyCode()) || obj.getManageAgencyCode().isEmpty()) {
            errorFieldList.add(new ErrorField("Agency", "Agency can't be empty"));
            return false;
        }
        if (!manageAgencyService.existByCode(this.upperCaseAndTrim(obj.getManageAgencyCode()))) {
            errorFieldList.add(new ErrorField("Agency", "Agency not exist"));
            return false;
        } else {
            try {
                ManageAgencyDto manageAgencyDto = manageAgencyService.findByCode(this.upperCaseAndTrim(obj.getManageAgencyCode()));
                if (Status.INACTIVE.name().equals(manageAgencyDto.getStatus())) {
                    errorFieldList.add(new ErrorField("Agency", "Agency is inactive"));
                    return false;
                }
            } catch (Exception e) {
                errorFieldList.add(new ErrorField("Agency", "Agency not found."));
                return false;
            }
        }

        return true;
    }

    private String upperCaseAndTrim(String code){
        String value = code.trim();
        return value.toUpperCase();
    }
}
