package com.kynsoft.finamer.payment.infrastructure.excel.validators.anti;

import com.kynsof.share.core.application.excel.validator.ICache;
import com.kynsof.share.core.application.excel.validator.IValidatorFactory;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailSimpleDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentAntiRowError;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.error.anti.PaymentImportAntiErrorEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.SecurityImportValidators;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentAntiValidatorFactory extends IValidatorFactory<AntiToIncomeRow> {

    private PaymentImportAmountValidator paymentImportAmountValidator;
    private PaymentTransactionIdValidator paymentTransactionIdValidator;

    private final IPaymentDetailService paymentDetailService;
    private final PaymentImportCacheRepository paymentImportCacheRepository;

    private final SecurityImportValidators securityImportValidators;
   // private PaymentTotalAmountValidator totalAmountValidator;

    public PaymentAntiValidatorFactory(ApplicationEventPublisher paymentEventPublisher,
                                       IPaymentDetailService paymentDetailService,
                                       PaymentImportCacheRepository paymentImportCacheRepository,
                                       SecurityImportValidators securityImportValidators
    ) {
        super(paymentEventPublisher);
        this.paymentDetailService = paymentDetailService;
        this.paymentImportCacheRepository = paymentImportCacheRepository;
        this.securityImportValidators = securityImportValidators;
    }

    @Override
    public void createValidators() {

    }

    @Override
    public void createValidators(ICache iCache) {
        Cache cache = (Cache)iCache;

        paymentTransactionIdValidator = new PaymentTransactionIdValidator(cache, applicationEventPublisher);
        paymentImportAmountValidator = new PaymentImportAmountValidator(applicationEventPublisher, paymentDetailService, paymentImportCacheRepository);

    }

    @Override
    public boolean validate(AntiToIncomeRow toValidate) {
       // totalAmountValidator.validate(toValidate.getAmount(), toValidate.getAmount(), toValidate.getImportProcessId());
        paymentTransactionIdValidator.validate(toValidate, errorFieldList);
        paymentImportAmountValidator.validate(toValidate, errorFieldList);
        PaymentDetailSimpleDto paymentDetailDto = paymentDetailService.findSimpleDetailByGenId(toValidate.getTransactionId().intValue());
        this.securityImportValidators.validateAgency(toValidate.getAgencys(), paymentDetailDto.getPaymentAgency(), errorFieldList);
        this.securityImportValidators.validateHotel(toValidate.getHotels(), paymentDetailDto.getPaymentHotel(), errorFieldList);

        if (this.hasErrors()) {
            PaymentImportAntiErrorEvent paymentImportErrorEvent =
                    new PaymentImportAntiErrorEvent(
                            new PaymentAntiRowError(null, toValidate.getRowNumber(), toValidate.getImportProcessId(),
                                    errorFieldList, toValidate));
            this.sendErrorEvent(paymentImportErrorEvent);
        }
        boolean result = !this.hasErrors();
        this.clearErrors();
        return result;

    }

    @Override
    public boolean validate(List<AntiToIncomeRow> toValidateList){
        int errors = 0;

        for (AntiToIncomeRow toValidateRow : toValidateList) {
            errors += paymentTransactionIdValidator.validate(toValidateRow, errorFieldList) ? 0 : 1;
        }
    }


}
