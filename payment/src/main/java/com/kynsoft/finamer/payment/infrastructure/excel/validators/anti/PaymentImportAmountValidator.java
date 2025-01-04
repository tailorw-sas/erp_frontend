package com.kynsoft.finamer.payment.infrastructure.excel.validators.anti;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Objects;

public class PaymentImportAmountValidator extends ExcelRuleValidator<AntiToIncomeRow> {

    private final IPaymentDetailService paymentDetailService;
    private final PaymentImportCacheRepository paymentImportCacheRepository;

    public PaymentImportAmountValidator(ApplicationEventPublisher applicationEventPublisher,
                                           IPaymentDetailService paymentDetailService,
                                           PaymentImportCacheRepository paymentImportCacheRepository) {
        super(applicationEventPublisher);
        this.paymentDetailService = paymentDetailService;
        this.paymentImportCacheRepository = paymentImportCacheRepository;
    }

    @Override
    public boolean validate(AntiToIncomeRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getAmount())) {
            errorFieldList.add(new ErrorField("Payment Amount", "Payment Amount can't be empty."));
            return false;
        }
        boolean valid = obj.getAmount() > 0;
        if (!valid) {
            errorFieldList.add(new ErrorField("Payment Amount", "Payment Amount must be greater than 0."));
            return false;
        }
        if (Objects.nonNull(obj.getTransactionId()) &&
                paymentDetailService.existByGenId(obj.getTransactionId().intValue())){
            Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "id"));

            Page<PaymentImportCache> pageCache;
            double amountTotal = 0;
            do {
                 pageCache = paymentImportCacheRepository.findAllByImportProcessId(obj.getImportProcessId(), pageable);
                amountTotal += pageCache.stream().filter(Objects::nonNull)
                        .filter(paymentImportCache -> Objects.nonNull(paymentImportCache.getAnti()) &&
                                !paymentImportCache.getAnti().isEmpty())
                        .map(paymentCache -> Double.parseDouble(paymentCache.getPaymentAmount()))
                        .reduce(0.0, Double::sum);
               pageable= pageable.next();
            } while (pageCache.hasNext());
            PaymentDetailDto paymentDetailDto = paymentDetailService.findByGenId(obj.getTransactionId().intValue());
            if (Objects.isNull(paymentDetailDto.getApplyDepositValue()) || obj.getAmount()+amountTotal > paymentDetailDto.getApplyDepositValue()){
                errorFieldList.add(new ErrorField("Payment Amount","Deposit Amount must be greather than zero and less or equal than the selected transaction amount."));
            }
        }
        return true;

    }

}


