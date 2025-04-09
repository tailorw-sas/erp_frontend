package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class PaymentDetailAntiExistsValidator extends ExcelRuleValidator<PaymentDetailRow> {

    private final Cache cache;

    public PaymentDetailAntiExistsValidator(ApplicationEventPublisher applicationEventPublisher,
                                            Cache cache){
        super(applicationEventPublisher);
        this.cache = cache;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList) {
        ManagePaymentTransactionTypeDto transactionType = this.cache.getManageTransactionTypeByCode(obj.getTransactionType());
        if(Objects.isNull(transactionType)){
            errorFieldList.add(new ErrorField("Transaction type", "Transaction type not found."));
            return false;
        }

        if(transactionType.getApplyDeposit()){
            if (Objects.isNull(obj.getAnti())) {
                errorFieldList.add(new ErrorField("Transaction id", "Transaction id can't be empty."));
                return false;
            }

            PaymentDetailDto paymentDetailDto = cache.getPaymentDetailByPaymentDetailId(obj.getAnti().longValue());
            if(Objects.isNull(paymentDetailDto)){
                errorFieldList.add(new ErrorField("Transaction id", "Payment Details not found: " + obj.getAnti().intValue()));
                return false;
            }

            if (!paymentDetailDto.getTransactionType().getDeposit()) {
                errorFieldList.add(new ErrorField("Transaction id", "Transaction isn't deposit type"));
                return false;
            }
        }

        return true;
    }
}
