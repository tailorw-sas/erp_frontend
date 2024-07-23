package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import org.springframework.context.ApplicationEventPublisher;

public class ImportBookingHotelInvoiceNumberValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageHotelService manageHotelService;

    public ImportBookingHotelInvoiceNumberValidator(ApplicationEventPublisher applicationEventPublisher, IManageHotelService manageHotelService) {
        super(applicationEventPublisher);
        this.manageHotelService = manageHotelService;
    }

    @Override
    public boolean validate(BookingRow obj) {
        ManageHotelDto manageHotelDto = manageHotelService.findByCode(obj.getManageHotelCode());
        if ( manageHotelDto.isVirtual() && (obj.getHotelInvoiceNumber().isEmpty() || Integer.parseInt(obj.getHotelInvoiceNumber()) ==0 )){
            sendErrorEvent(obj.getRowNumber(),new ErrorField("HotelInvoiceNumber","Hotel Invoice Number must not 0 if hotel is virtual"),obj);
            return false;
        }
        return true;
    }


}
