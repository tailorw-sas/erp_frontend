package com.kynsoft.finamer.payment.infrastructure.excel.validators.anti;

import com.kynsof.share.core.application.excel.validator.IValidatorFactory;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportError;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentAntiRowError;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.PaymentImportErrorEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.event.error.anti.PaymentImportAntiErrorEvent;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PaymentAntiValidatorFactory extends IValidatorFactory<AntiToIncomeRow> {

    private PaymentImportAmountValidator paymentImportAmountValidator;
    private PaymentTransactionIdValidator paymentTransactionIdValidator;

    private final IPaymentDetailService paymentDetailService;
    private final PaymentImportCacheRepository paymentImportCacheRepository;

   // private PaymentTotalAmountValidator totalAmountValidator;

    public PaymentAntiValidatorFactory(ApplicationEventPublisher paymentEventPublisher,
                                       IPaymentDetailService paymentDetailService,
                                       PaymentImportCacheRepository paymentImportCacheRepository
    ) {
        super(paymentEventPublisher);
        this.paymentDetailService = paymentDetailService;
        this.paymentImportCacheRepository = paymentImportCacheRepository;
    }

    @Override
    public void createValidators() {
        paymentImportAmountValidator = new PaymentImportAmountValidator(applicationEventPublisher, paymentDetailService, paymentImportCacheRepository);
        paymentTransactionIdValidator = new PaymentTransactionIdValidator(paymentDetailService, applicationEventPublisher);
      //  totalAmountValidator = new PaymentTotalAmountValidator(paymentImportCacheRepository);
    }

    @Override
    public boolean validate(AntiToIncomeRow toValidate) {
       // totalAmountValidator.validate(toValidate.getAmount(), toValidate.getAmount(), toValidate.getImportProcessId());
        paymentTransactionIdValidator.validate(toValidate, errorFieldList);
        paymentImportAmountValidator.validate(toValidate, errorFieldList);
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


}
