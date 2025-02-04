package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EImportType;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.infrastructure.utils.InvoiceUtils;

import java.util.List;
import java.util.Objects;

public class ImportBookingTypeValidator  extends ExcelRuleValidator<BookingRow> {
    private final String importType;
    private final IManageHotelService manageHotelService;

    public ImportBookingTypeValidator(String importType, IManageHotelService manageHotelService) {
        this.importType = importType;
        this.manageHotelService = manageHotelService;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        if (Objects.nonNull(obj.getManageHotelCode()) && !obj.getManageHotelCode().isEmpty() && manageHotelService.existByCode(InvoiceUtils.upperCaseAndTrim(obj.getManageHotelCode()))) {
            ManageHotelDto manageHotelDto = manageHotelService.findByCode(InvoiceUtils.upperCaseAndTrim(obj.getManageHotelCode()));
            if ((EImportType.NO_VIRTUAL.name().equals(importType) || EImportType.INNSIST.name().equals(importType)) && manageHotelDto.isVirtual()) {
                errorFieldList.add(new ErrorField("Import type", "The hotel is virtual"));
                return false;
            }
            if (EImportType.VIRTUAL.name().equals(importType) && !manageHotelDto.isVirtual()) {
                errorFieldList.add(new ErrorField("Import type", "The hotel isn't virtual"));
                return false;
            }
        }else{
            //errorFieldList.add(new ErrorField("Hotel", " Hotel not found."));
            return false;
        }
        return true;
    }

}
