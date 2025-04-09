package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelListRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.RowErrorField;
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

    public PaymentDetailAmountBalanceValidator(ApplicationEventPublisher applicationEventPublisher,
                                               Cache cache){
        super(applicationEventPublisher);
        this.cache = cache;
    }

    @Override
    public void validate(List<PaymentDetailRow> obj, List<RowErrorField> errorFieldList) {
        ManagePaymentTransactionTypeDto cashTransactionType = this.cache.getCashTransactionType();

        Map<String, List<PaymentDetailRow>> paymentIdGroups = obj.stream()
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
                        errorFieldList.add(new RowErrorField(paymentDetailRow.getRowNumber(), getErrorListField()))
                );
            }
        }
    }

    private List<ErrorField> getErrorListField(){
        ErrorField error = new ErrorField("Payment detail balance", "The payment balance is less than the sum of the records in the file.");
        List<ErrorField> list = new ArrayList<>();
        list.add(error);
        return list;
    }
}
