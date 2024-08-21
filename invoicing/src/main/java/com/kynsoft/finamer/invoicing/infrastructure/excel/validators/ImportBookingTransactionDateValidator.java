package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceCloseOperationDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.excel.util.DateUtil;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceCloseOperationService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


public class ImportBookingTransactionDateValidator extends ExcelRuleValidator<BookingRow> {

    private final IInvoiceCloseOperationService closeOperationService;
    private final IManageHotelService hotelService;

    public ImportBookingTransactionDateValidator(IInvoiceCloseOperationService closeOperationService,
                                                 IManageHotelService hotelService) {
        this.closeOperationService = closeOperationService;
        this.hotelService = hotelService;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        return this.validateDateFormat(obj,errorFieldList) && this.validateCloseOperation(obj, errorFieldList);
    }

    private boolean validateDateFormat(BookingRow obj,List<ErrorField> errorFieldList){
        if (Objects.isNull(obj.getTransactionDate()) || obj.getTransactionDate().isEmpty()){
            errorFieldList.add(new ErrorField("Transaction Date","Transaction Date can't be empty"));
            return false;
        }
       if(!DateUtil.validateDateFormat(obj.getTransactionDate())){
           errorFieldList.add(new ErrorField("Transaction Date","Transaction Date has invalid date format"));
           return false;
       }
        LocalDate dateToValidate= DateUtil.parseDateToLocalDate(obj.getTransactionDate());
        if (dateToValidate.isAfter(LocalDate.now())) {
            errorFieldList.add(new ErrorField("Transaction Date", "Transaction Date can't be future date"));
            return false;
        }
       return true;
    }
    private boolean validateCloseOperation(BookingRow bookingRow,List<ErrorField> errorFieldList){
        if (!hotelService.existByCode(bookingRow.getManageHotelCode())){
            return false;
        }
        ManageHotelDto manageHotelDto = hotelService.findByCode(bookingRow.getManageHotelCode());
        InvoiceCloseOperationDto invoiceCloseOperationDto = closeOperationService.findActiveByHotelId(manageHotelDto.getId());
        LocalDate beginDate= invoiceCloseOperationDto.getBeginDate();
        LocalDate endDate = invoiceCloseOperationDto.getEndDate();
        LocalDate transactionDate = DateUtil.parseDateToLocalDate(bookingRow.getTransactionDate());
        boolean result= transactionDate.isBefore(beginDate) || transactionDate.isAfter(endDate);
        if (result){
            errorFieldList.add(new ErrorField("Transaction Date","Transaction Date is out of close operation"));
        }
        return !result;
    }
}
