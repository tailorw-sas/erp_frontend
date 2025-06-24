package com.kynsoft.finamer.payment.infrastructure.excel.validators.anti;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailSimpleDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class PaymentTransactionIdValidator extends ExcelRuleValidator<AntiToIncomeRow> {

    private final Cache cache;

    public PaymentTransactionIdValidator(Cache cache, ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
        this.cache = cache;
    }

    @Override
    public boolean validate(AntiToIncomeRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getTransactionId())) {
            errorFieldList.add(new ErrorField("Transaction id", "Transaction id can't be empty."));
            return false;
        }

        PaymentDetailDto paymentDetailDto = this.cache.getPaymentDetailByPaymentDetailId(obj.getTransactionId().longValue());

        if (Objects.isNull(paymentDetailDto)) {
            errorFieldList.add(new ErrorField("Transaction id", "There isn't payment detail with this transaction id"));
            return false;
        }

        try {
            if (!paymentDetailDto.getTransactionType().getDeposit()) {
                errorFieldList.add(new ErrorField("Transaction id", "Transaction isn't deposit type"));
                return false;
            }
        } catch (Exception e) {
            errorFieldList.add(new ErrorField("Transaction id", "Payment Details not found: " + obj.getTransactionId().intValue()));
            return false;
        }

        return true;
    }
}
