package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;

import java.util.List;
import java.util.Objects;

public class ImportBookingHotelInvoiceNumberValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageHotelService manageHotelService;
    private final IManageBookingService manageBookingService;

    public ImportBookingHotelInvoiceNumberValidator(IManageHotelService manageHotelService, IManageBookingService manageBookingService) {
        this.manageHotelService = manageHotelService;
        this.manageBookingService = manageBookingService;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        ManageHotelDto manageHotelDto = manageHotelService.findByCode(obj.getManageHotelCode());
        if (manageHotelDto.isVirtual() && Objects.isNull(obj.getHotelInvoiceNumber())) {
            errorFieldList.add(new ErrorField("HotelInvoiceNumber", " Hotel Invoice Number can't be empty"));
            return false;
        }
        if (!manageHotelService.existByCode(obj.getManageHotelCode())) {
            return false;
        }

        if (manageHotelDto.isVirtual() && (obj.getHotelInvoiceNumber().isEmpty() || Integer.parseInt(obj.getHotelInvoiceNumber()) == 0)) {
            errorFieldList.add(new ErrorField("HotelInvoiceNumber", "Hotel Invoice Number must not 0 if hotel is virtual"));
            return false;
        }

        if (manageHotelDto.isVirtual() && manageBookingService.existsByHotelInvoiceNumber(obj.getHotelInvoiceNumber(), manageHotelDto.getId())) {
            errorFieldList.add(new ErrorField("HotelInvoiceNumber", "Hotel Invoice Number already exists"));
            return false;
        }
        return true;
    }

}
