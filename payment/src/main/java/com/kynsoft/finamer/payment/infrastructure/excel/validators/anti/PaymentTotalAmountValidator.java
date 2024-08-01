package com.kynsoft.finamer.payment.infrastructure.excel.validators.anti;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.AntiToIncomeRow;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
public class PaymentTotalAmountValidator {

    private final PaymentImportCacheRepository paymentImportCacheRepository;

    protected PaymentTotalAmountValidator( PaymentImportCacheRepository paymentImportCacheRepository) {
        this.paymentImportCacheRepository = paymentImportCacheRepository;
    }


    public void validate(double totalAmount,String importProcessId) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "id"));

        Page<PaymentImportCache> pageCache;
        double amountTotal = 0;
        do {
            pageCache = paymentImportCacheRepository.findAllByImportProcessId(importProcessId, pageable);
            amountTotal += pageCache.stream().filter(Objects::nonNull).map(paymentCache -> Double.parseDouble(paymentCache.getPaymentAmount())).reduce(0.0, Double::sum);
            pageable.next();
        } while (pageCache.hasNext());
       if (amountTotal != totalAmount){
           throw new BusinessException(DomainErrorMessage.AMOUNT_MISMATCH,"The total amount isn't equals to the imported total amount");
       }
    }
}
