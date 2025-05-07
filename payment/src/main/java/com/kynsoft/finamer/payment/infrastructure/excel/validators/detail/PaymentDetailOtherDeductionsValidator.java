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

public class PaymentDetailOtherDeductionsValidator extends ExcelListRuleValidator<PaymentDetailRow> {

    private final Cache cache;

    protected PaymentDetailOtherDeductionsValidator(ApplicationEventPublisher applicationEventPublisher,
                                                    Cache cache) {
        super(applicationEventPublisher);
        this.cache = cache;
    }

    @Override
    public void validate(List<PaymentDetailRow> paymentDetailRows, List<RowErrorField> errorRowList) {
        ManagePaymentTransactionTypeDto cachPaymentTransactionType = cache.getCashTransactionType();
        ManagePaymentTransactionTypeDto applyInvoicePaymentTransactionType = cache.getPaymentInvoiceTransactionType();
        ManagePaymentTransactionTypeDto antiPaymentTransactionType = cache.getApplyDepositTransactionType();

        this.validateBookingById(paymentDetailRows, errorRowList,
                cachPaymentTransactionType, applyInvoicePaymentTransactionType, antiPaymentTransactionType);

        this.validateBookingByCuopon(paymentDetailRows, errorRowList,
                cachPaymentTransactionType, applyInvoicePaymentTransactionType, antiPaymentTransactionType);
    }

    private void validateBookingById(List<PaymentDetailRow> paymentDetailRows,
                                     List<RowErrorField> errorRowList,
                                     ManagePaymentTransactionTypeDto cachPaymentTransactionType,
                                     ManagePaymentTransactionTypeDto applyInvoicePaymentTransactionType,
                                     ManagePaymentTransactionTypeDto antiPaymentTransactionType){

        Map<String, List<PaymentDetailRow>> paymentsGroupByBookingIdMap = paymentDetailRows.stream()
                .filter(paymentDetailRow -> Objects.nonNull(paymentDetailRow.getBookId())
                                                            && !paymentDetailRow.getBookId().isEmpty()
                        && Objects.nonNull(paymentDetailRow.getTransactionType()) && !paymentDetailRow.getTransactionType().isEmpty()
                && !paymentDetailRow.getTransactionType().equals(cachPaymentTransactionType.getCode())
                && !paymentDetailRow.getTransactionType().equals(applyInvoicePaymentTransactionType.getCode())
                && !paymentDetailRow.getTransactionType().equals(antiPaymentTransactionType.getCode()))
                .collect(Collectors.groupingBy(PaymentDetailRow::getBookId, Collectors.toList()));

        for(Map.Entry<String, List<PaymentDetailRow>> entry : paymentsGroupByBookingIdMap.entrySet()){
            ManageBookingDto bookingDto = cache.getBooking(Long.parseLong(entry.getKey()));
            validateBookingGroup(bookingDto, entry.getValue(), errorRowList);
        }
    }

    private void validateBookingByCuopon(List<PaymentDetailRow> paymentDetailRows,
                                     List<RowErrorField> errorRowList,
                                     ManagePaymentTransactionTypeDto cachPaymentTransactionType,
                                     ManagePaymentTransactionTypeDto applyInvoicePaymentTransactionType,
                                     ManagePaymentTransactionTypeDto antiPaymentTransactionType){

        Map<String, List<PaymentDetailRow>> paymentsGroupByBookingIdMap = paymentDetailRows.stream()
                .filter(paymentDetailRow -> Objects.nonNull(paymentDetailRow.getCoupon())
                        && !paymentDetailRow.getCoupon().isEmpty()
                        && !paymentDetailRow.getTransactionType().equals(cachPaymentTransactionType.getCode())
                        && !paymentDetailRow.getTransactionType().equals(applyInvoicePaymentTransactionType.getCode())
                        && !paymentDetailRow.getTransactionType().equals(antiPaymentTransactionType.getCode()))
                .collect(Collectors.groupingBy(PaymentDetailRow::getCoupon, Collectors.toList()));

        for(Map.Entry<String, List<PaymentDetailRow>> entry : paymentsGroupByBookingIdMap.entrySet()){
            List<ManageBookingDto> bookingsByCuopon = cache.getBookingsByCoupon(entry.getKey());
            if(Objects.nonNull(bookingsByCuopon) && bookingsByCuopon.size() == 1){
                validateBookingGroup(bookingsByCuopon.get(0), entry.getValue(), errorRowList);
            }
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
