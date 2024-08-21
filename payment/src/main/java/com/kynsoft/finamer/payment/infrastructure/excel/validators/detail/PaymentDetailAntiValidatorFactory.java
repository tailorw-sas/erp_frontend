package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.application.excel.validator.IValidatorFactory;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentAntiRowError;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentDetailRowError;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.error.anti.PaymentImportAntiErrorEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.event.error.detail.PaymentImportDetailErrorEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.anti.PaymentAntiValidatorFactory;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.anti.PaymentImportAmountValidator;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.anti.PaymentTotalAmountValidator;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.anti.PaymentTransactionIdValidator;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class PaymentDetailAntiValidatorFactory extends IValidatorFactory<PaymentDetailRow> {

    private PaymentImportAmountValidator paymentImportAmountValidator;
    private PaymentTransactionIdValidator paymentTransactionIdValidator;

    private PaymentDetailExistPaymentValidator paymentDetailExistPaymentValidator;
    private final IPaymentDetailService paymentDetailService;
    private final PaymentImportCacheRepository paymentImportCacheRepository;

    private final IPaymentService paymentService;

    public PaymentDetailAntiValidatorFactory(IPaymentDetailService paymentDetailService,
                                             PaymentImportCacheRepository paymentImportCacheRepository,
                                             ApplicationEventPublisher applicationEventPublisher,
                                             IPaymentService paymentService) {
        super(applicationEventPublisher);
        this.paymentDetailService = paymentDetailService;
        this.paymentImportCacheRepository = paymentImportCacheRepository;
        this.paymentService = paymentService;
    }

    @Override
    public void createValidators() {
        paymentDetailExistPaymentValidator=new PaymentDetailExistPaymentValidator(applicationEventPublisher,paymentService);
        paymentImportAmountValidator = new PaymentImportAmountValidator(applicationEventPublisher, paymentDetailService, paymentImportCacheRepository);
        paymentTransactionIdValidator = new PaymentTransactionIdValidator(paymentDetailService, applicationEventPublisher);
    }

    @Override
    public boolean validate(PaymentDetailRow toValidate) {
        paymentDetailExistPaymentValidator.validate(toValidate,errorFieldList);
        AntiToIncomeRow antiToIncomeRow = new AntiToIncomeRow();
        antiToIncomeRow.setTransactionId(toValidate.getAnti());
        antiToIncomeRow.setAmount(toValidate.getBalance());
        antiToIncomeRow.setRemarks(toValidate.getRemarks());
        antiToIncomeRow.setImportProcessId(toValidate.getImportProcessId());
        paymentTransactionIdValidator.validate(antiToIncomeRow, errorFieldList);
        paymentImportAmountValidator.validate(antiToIncomeRow, errorFieldList);
        if (this.hasErrors()) {
            PaymentImportDetailErrorEvent paymentImportErrorEvent =
                    new PaymentImportDetailErrorEvent(
                            new PaymentDetailRowError(null, toValidate.getRowNumber(), toValidate.getImportProcessId(),
                                    errorFieldList, toValidate));
            this.sendErrorEvent(paymentImportErrorEvent);
        }
        boolean result = !this.hasErrors();
        this.clearErrors();
        return result;
    }
}
