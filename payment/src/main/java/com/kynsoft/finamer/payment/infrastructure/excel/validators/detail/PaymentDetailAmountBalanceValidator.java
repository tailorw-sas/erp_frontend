package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelListRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.RowErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PaymentDetailAmountBalanceValidator extends ExcelListRuleValidator<PaymentDetailRow> {

    private final Cache cache;

    public PaymentDetailAmountBalanceValidator(Cache cache){
        this.cache = cache;
    }

    @Override
    public void validate(List<PaymentDetailRow> paymentDetailRows, List<RowErrorField> errorFieldList) {
        //this.validateBookingAmount(paymentDetailRows, errorFieldList);
        this.validateCashAmount(paymentDetailRows, errorFieldList);
    }

    private void validateBookingAmount(List<PaymentDetailRow> paymentDetailRows, List<RowErrorField> errorFieldList){
        this.validateBookingAmountByBookingId(paymentDetailRows, errorFieldList);
        this.validateBookingAmountByCuopon(paymentDetailRows, errorFieldList);
    }

    private void validateBookingAmountByBookingId(List<PaymentDetailRow> paymentDetailRows, List<RowErrorField> errorFieldList){
        Map<String, List<PaymentDetailRow>> bookingIdGroups = paymentDetailRows.stream()
                .filter(paymentDetailRow -> Objects.nonNull(paymentDetailRow.getTransactionType())
                        && !paymentDetailRow.getTransactionType().isEmpty()
                        && Objects.nonNull(paymentDetailRow.getBookId()) && !paymentDetailRow.getBookId().isEmpty()
                )
                .collect(Collectors.groupingBy(PaymentDetailRow::getBookId));

        for(Map.Entry<String, List<PaymentDetailRow>> bookingGroup : bookingIdGroups.entrySet()){
            ManageBookingDto bookingDto = cache.getBooking(Long.parseLong(bookingGroup.getKey()));
            validateBooking(bookingDto, bookingGroup.getValue(), errorFieldList);
        }
    }

    private void validateBookingAmountByCuopon(List<PaymentDetailRow> paymentDetailRows, List<RowErrorField> errorFieldList){
        Map<String, List<PaymentDetailRow>> bookingIdGroups = paymentDetailRows.stream()
                .filter(paymentDetailRow -> Objects.nonNull(paymentDetailRow.getTransactionType())
                        && !paymentDetailRow.getTransactionType().isEmpty()
                        && Objects.nonNull(paymentDetailRow.getCoupon()) && !paymentDetailRow.getCoupon().isEmpty()
                )
                .collect(Collectors.groupingBy(PaymentDetailRow::getCoupon));

        for(Map.Entry<String, List<PaymentDetailRow>> cuoponGroup : bookingIdGroups.entrySet()){
            List<ManageBookingDto> bookingsByCuopon = cache.getBookingsByCoupon(cuoponGroup.getKey());
            if(Objects.nonNull(bookingsByCuopon) && bookingsByCuopon.size() == 1){
                validateBooking(bookingsByCuopon.get(0), cuoponGroup.getValue(), errorFieldList);
            }
        }
    }

    private void validateBooking(ManageBookingDto booking, List<PaymentDetailRow> details, List<RowErrorField> errorFieldList){
        if(Objects.nonNull(booking)){
            double totalAmount = details.stream()
                    .mapToDouble(PaymentDetailRow::getBalance)
                    .sum();
            if(totalAmount > booking.getAmountBalance()){
                details.forEach(paymentDetailRow ->
                        errorFieldList.add(new RowErrorField(paymentDetailRow.getRowNumber(), getErrorListField(new ErrorField("Booking amount balance", "The booking amount balance is less than the sum of the records in the file."))))
                );
            }
        }
    }

    private void validateCashAmount(List<PaymentDetailRow> paymentDetailRows, List<RowErrorField> errorFieldList){
        ManagePaymentTransactionTypeDto cashTransactionType = this.cache.getCashTransactionType();

        Map<String, List<PaymentDetailRow>> paymentIdGroups = paymentDetailRows.stream()
                .filter(paymentDetailRow -> paymentDetailRow.getTransactionType() != null
                        && cashTransactionType.getCode() != null
                        && paymentDetailRow.getTransactionType().trim().equals(cashTransactionType.getCode()))
                .collect(Collectors.groupingBy(PaymentDetailRow::getPaymentId));

        for(Map.Entry<String, List<PaymentDetailRow>> group : paymentIdGroups.entrySet()){
            validateGroup(group.getKey().trim(), group.getValue(), errorFieldList);
        }
    }

    private void validateGroup(String paymentId, List<PaymentDetailRow> details, List<RowErrorField> errorFieldList){
        PaymentDto payment = this.cache.getPaymentByPaymentId(Long.parseLong(paymentId));
        if(Objects.nonNull(payment)){
            double totalAmount = details.stream()
                    .mapToDouble(PaymentDetailRow::getBalance)
                    .sum();

            if(totalAmount > payment.getPaymentBalance()){
                details.forEach(paymentDetailRow ->
                        errorFieldList.add(new RowErrorField(paymentDetailRow.getRowNumber(), getErrorListField(new ErrorField("Payment detail balance", "The payment balance is less than the sum of the records in the file."))))
                );
            }
        }
    }
}
