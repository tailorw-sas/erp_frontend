package com.kynsoft.finamer.payment.infrastructure.excel.validators.anti;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportError;
import com.kynsoft.finamer.payment.domain.excel.bean.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.bean.PaymentBankRow;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PaymentImportAmountValidator extends ExcelRuleValidator<AntiToIncomeRow> {

    private final IPaymentDetailService paymentDetailService;
    private final PaymentImportCacheRepository paymentImportCacheRepository;

    protected PaymentImportAmountValidator(ApplicationEventPublisher applicationEventPublisher,
                                           IPaymentDetailService paymentDetailService,
                                           PaymentImportCacheRepository paymentImportCacheRepository) {
        super(applicationEventPublisher);
        this.paymentDetailService = paymentDetailService;
        this.paymentImportCacheRepository = paymentImportCacheRepository;
    }

    @Override
    public boolean validate(AntiToIncomeRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getAmount())) {
            errorFieldList.add(new ErrorField("Payment Amount", "Payment Amount can't be empty"));
            return false;
        }
        boolean valid = obj.getAmount() != 0;
        if (!valid) {
            errorFieldList.add(new ErrorField("Payment Amount", "Payment Amount must not be 0"));
            return false;
        }
        if (errorFieldList.stream().noneMatch(errorField -> "Transaction id".equals(errorField.getField()))){
            Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "id"));

            Page<PaymentImportCache> pageCache;
            double amountTotal = 0;
            do {
                pageCache = paymentImportCacheRepository.findAllByImportProcessId(obj.getImportProcessId(), pageable);
                amountTotal += pageCache.stream().filter(Objects::nonNull).map(paymentCache -> Double.parseDouble(paymentCache.getPaymentAmount())).reduce(0.0, Double::sum);
                pageable.next();
            } while (pageCache.hasNext());
            PaymentDetailDto paymentDetailDto = paymentDetailService.findByGenId(Integer.parseInt(obj.getTransactionId()));
            if (obj.getAmount() > paymentDetailDto.getPayment().getPaymentBalance()) {
                errorFieldList.add(new ErrorField("Payment Amount", "Payment Amount is greater than payment balance"));
                return false;
            }
            amountTotal = amountTotal + obj.getAmount();
            if (amountTotal > paymentDetailDto.getPayment().getDepositBalance()) {
                errorFieldList.add(new ErrorField("Payment Amount", "Payment Amount is greater than deposit balance"));
                return false;
            }
        }
        return true;

    }

}


