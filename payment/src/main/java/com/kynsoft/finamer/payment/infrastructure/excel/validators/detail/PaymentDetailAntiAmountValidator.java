package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelListRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.RowErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.*;
import java.util.stream.Collectors;

public class PaymentDetailAntiAmountValidator extends ExcelListRuleValidator<PaymentDetailRow> {

    private final Cache cache;

    protected PaymentDetailAntiAmountValidator(ApplicationEventPublisher applicationEventPublisher,
                                               Cache cache) {
        super(applicationEventPublisher);
        this.cache = cache;
    }

    @Override
    public void validate(List<PaymentDetailRow> objList, List<RowErrorField> errorFieldList) {
        ManagePaymentTransactionTypeDto depositTransactionType = this.cache.getApplyDepositTransactionType();

        Map<String, Map<Double, List<PaymentDetailRow>>> paymentByPaymentDetailMap = objList.stream()
                .filter(paymentDetailRow -> Objects.nonNull(paymentDetailRow.getTransactionType())
                        && !paymentDetailRow.getTransactionType().isEmpty()
                        && paymentDetailRow.getTransactionType().equals(depositTransactionType.getCode()))
                .collect(Collectors.groupingBy(PaymentDetailRow::getPaymentId, Collectors.groupingBy(PaymentDetailRow::getAnti, Collectors.toList())));

        for(Map.Entry<String, Map<Double, List<PaymentDetailRow>>> paymentMap : paymentByPaymentDetailMap.entrySet()){
            PaymentDto payment = this.cache.getPaymentByPaymentId(Long.parseLong(paymentMap.getKey()));
            validatePaymentGroup(payment, paymentMap.getValue(), errorFieldList);
        }
    }

    private void validatePaymentGroup(PaymentDto payment, Map<Double, List<PaymentDetailRow>> detailRowMap, List<RowErrorField> rowErrorFieldList){
        if(Objects.nonNull(payment)){
            for(Map.Entry<Double, List<PaymentDetailRow>> depositPaymentDetailMap : detailRowMap.entrySet()){
                PaymentDetailDto depositPaymentDetail = this.cache.getPaymentDetailByPaymentDetailId(depositPaymentDetailMap.getKey().longValue());
                validateDepositBalance(payment, depositPaymentDetail, depositPaymentDetailMap.getValue(), rowErrorFieldList);
            }
        }else{
            ErrorField errorField = new ErrorField("PaymentId", "Payment ID not found");
            for(Map.Entry<Double, List<PaymentDetailRow>> depositPaymentDetailMap : detailRowMap.entrySet()){
                List<Integer> rowNumberList = depositPaymentDetailMap.getValue().stream().map(PaymentDetailRow::getRowNumber).toList();
                addErrorsToRowList(rowErrorFieldList, rowNumberList, errorField);
            }
        }
    }

    private void validateDepositBalance(PaymentDto payment, PaymentDetailDto depositPaymentDetail, List<PaymentDetailRow> paymentDetails, List<RowErrorField> rowErrorFieldList){
        List<Integer> rowNumberList = paymentDetails.stream().map(PaymentDetailRow::getRowNumber).toList();

        if(Objects.isNull(depositPaymentDetail)){
            ErrorField errorField = new ErrorField("AntiId", "Anti ID not found");
            addErrorsToRowList(rowErrorFieldList, rowNumberList, errorField);
            return;
        }

        if(depositPaymentDetail.getPayment().getPaymentId() != payment.getPaymentId()){
            ErrorField errorField = new ErrorField("AntiId", "Anti ID does not belong to the payment");
            addErrorsToRowList(rowErrorFieldList, rowNumberList, errorField);
            return;
        }

        double amount = paymentDetails.stream()
                .mapToDouble(PaymentDetailRow::getBalance)
                .sum();

        if(amount > depositPaymentDetail.getApplyDepositValue()){
            ErrorField errorField = new ErrorField("Payment Amount", "Deposit Amount must be greather than zero and less or equal than the selected transaction amount.");
            addErrorsToRowList(rowErrorFieldList, rowNumberList, errorField);
        }
    }
}
