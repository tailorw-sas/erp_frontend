package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class ImportBookingHotelInvoiceNumberValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageHotelService manageHotelService;

    public ImportBookingHotelInvoiceNumberValidator(IManageHotelService manageHotelService) {
        this.manageHotelService = manageHotelService;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getHotelInvoiceNumber())){
            errorFieldList.add(new ErrorField("Hotel Booking No"," Hotel Booking No. can't be empty"));
            return false;
        }
       if(!manageHotelService.existByCode(obj.getManageHotelCode())){
           return false;
       }
        ManageHotelDto manageHotelDto = manageHotelService.findByCode(obj.getManageHotelCode());
        if ( manageHotelDto.isVirtual() && (obj.getHotelInvoiceNumber().isEmpty() || Integer.parseInt(obj.getHotelInvoiceNumber()) ==0 )){
            errorFieldList.add(new ErrorField("HotelInvoiceNumber","Hotel Invoice Number must not 0 if hotel is virtual"));
            return false;
        }
        return true;
    }


}
