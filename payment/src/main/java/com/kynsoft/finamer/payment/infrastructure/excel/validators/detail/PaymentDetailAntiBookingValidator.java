package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelListRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.RowErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PaymentDetailAntiBookingValidator extends ExcelListRuleValidator<PaymentDetailRow> {

    private final Cache cache;

    public PaymentDetailAntiBookingValidator(ApplicationEventPublisher applicationEventPublisher,
                                             Cache cache){
        super(applicationEventPublisher);
        this.cache = cache;
    }

    @Override
    public void validate(List<PaymentDetailRow> obj, List<RowErrorField> errorRowList) {
        ManagePaymentTransactionTypeDto depositTransactionType = this.cache.getApplyDepositTransactionType();

        Map<String, List<PaymentDetailRow>> paymentDetailsByBookingMap = obj.stream()
                .filter(paymentDetailRow -> Objects.nonNull(paymentDetailRow.getTransactionType())
                        && !paymentDetailRow.getTransactionType().isEmpty()
                        && paymentDetailRow.getTransactionType().equals(depositTransactionType.getCode())
                        && Objects.nonNull(paymentDetailRow.getBookId()) && !paymentDetailRow.getBookId().isEmpty())
                .collect(Collectors.groupingBy(PaymentDetailRow::getBookId, Collectors.toList()));

        for(Map.Entry<String, List<PaymentDetailRow>> bookingMap : paymentDetailsByBookingMap.entrySet()){
            ManageBookingDto booking = this.cache.getBooking(Long.parseLong(bookingMap.getKey()));
            validateBookingGroup(booking, bookingMap.getValue(), errorRowList);
        }
    }

    private void validateBookingGroup(ManageBookingDto booking, List<PaymentDetailRow> paymentDetails, List<RowErrorField> rowErrorFieldList){
        List<Integer> rowNumberList = paymentDetails.stream().map(PaymentDetailRow::getRowNumber).toList();
        if(Objects.nonNull(booking)){
            double bookingAmount = paymentDetails.stream()
                    .mapToDouble(PaymentDetailRow::getBalance)
                    .sum();
            if(bookingAmount > booking.getAmountBalance()){
                ErrorField errorField = new ErrorField("Booking Balance", "The selected transaction amount must be less or equal than the invoice balance");
                addErrorsToRowList(rowErrorFieldList, rowNumberList, errorField);
            }
        }else{
            ErrorField errorField = new ErrorField("BookingId", "Booking ID not found");
            addErrorsToRowList(rowErrorFieldList, rowNumberList, errorField);
        }
    }
}
