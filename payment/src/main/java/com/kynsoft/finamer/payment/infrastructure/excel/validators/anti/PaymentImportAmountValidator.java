package com.kynsoft.finamer.payment.infrastructure.excel.validators.anti;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.application.excel.validator.ICache;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
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
        return true;
    }

    @Override
    public boolean validate(AntiToIncomeRow obj, List<ErrorField> errorFieldList, ICache icache) {
        Cache cache = (Cache)icache;

        if (Objects.isNull(obj.getAmount())) {
            errorFieldList.add(new ErrorField("Payment Amount", "Payment Amount can't be empty."));
            return false;
        }

        if (!(obj.getAmount() > 0)){
            errorFieldList.add(new ErrorField("Payment Amount", "Payment Amount must be greater than 0."));
            return false;
        }

        if(Objects.nonNull(obj.getTransactionId())){
            PaymentDetailDto paymentDetail = cache.getPaymentDetailByPaymentDetailId(obj.getTransactionId().longValue());
            if (Objects.nonNull(paymentDetail)) {
                if(Objects.isNull(paymentDetail.getPayment()) || paymentDetail.getPayment().getPaymentId() != Long.parseLong(obj.getPaymentId())){
                    errorFieldList.add(new ErrorField("Payment", "The specified Payment is not associated with the given Payment Detail."));
                    return false;
                }

                Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "id"));

                Page<PaymentImportCache> pageCache;
                double amountTotal = 0;
                do {
                    pageCache = paymentImportCacheRepository.findAllByImportProcessId(obj.getImportProcessId(), pageable);
                    amountTotal += pageCache.stream().filter(Objects::nonNull)
                            .filter(paymentImportCache -> Objects.nonNull(paymentImportCache.getAnti())
                                    && !paymentImportCache.getAnti().isEmpty()
                                    && Long.parseLong(paymentImportCache.getPaymentId()) == paymentDetail.getPayment().getPaymentId())
                            .map(paymentCache -> Double.parseDouble(paymentCache.getPaymentAmount()))
                            .reduce(0.0, Double::sum);
                    pageable = pageable.next();
                } while (pageCache.hasNext());

                if (Objects.isNull(paymentDetail.getApplyDepositValue()) || obj.getAmount() + amountTotal > paymentDetail.getApplyDepositValue()) {
                    errorFieldList.add(new ErrorField("Payment Amount", "Deposit Amount must be greather than zero and less or equal than the selected transaction amount."));
                    return false;
                }
            }else{
                errorFieldList.add(new ErrorField("Payment Details", "Payment Details not found."));
                return false;
            }
        }

        return true;
    }
}
