package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelListRuleValidator;
import com.kynsof.share.core.domain.response.RowErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

public class PaymentDetailOtherDeductionsValidator extends ExcelListRuleValidator<PaymentDetailRow> {

    private final Cache cache;

    protected PaymentDetailOtherDeductionsValidator(ApplicationEventPublisher applicationEventPublisher,
                                                    Cache cache) {
        super(applicationEventPublisher);
        this.cache = cache;
    }

    @Override
    public void validate(List<PaymentDetailRow> obj, List<RowErrorField> errorRowList) {
        //Map<>
    }
}
