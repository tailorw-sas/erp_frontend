package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.IValidatorFactory;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentDetailRowError;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.error.detail.PaymentImportDetailErrorEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.anti.PaymentImportAmountValidator;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.anti.PaymentTransactionIdValidator;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PaymentDetailValidatorFactory extends IValidatorFactory<PaymentDetailRow> {

    private PaymentImportDetailAmountValidator paymentImportDetailAmountValidator;
    private PaymentDetailsNoApplyDepositValidator paymentDetailsNoApplyDepositValidator;

    private PaymentDetailExistPaymentValidator paymentDetailExistPaymentValidator;

    private PaymentDetailBelongToSamePayment paymentDetailBelongToSamePayment;

    private PaymentImportAmountValidator paymentImportAmountValidator;
    private PaymentTransactionIdValidator paymentTransactionIdValidator;

    private final IPaymentService paymentService;

    private final IPaymentDetailService paymentDetailService;

    private final PaymentImportCacheRepository paymentImportCacheRepository;

    private final IManagePaymentTransactionTypeService managePaymentTransactionTypeService;


    public PaymentDetailValidatorFactory(ApplicationEventPublisher paymentEventPublisher, IPaymentService paymentService,
                                         IPaymentDetailService paymentDetailService,
                                         PaymentImportCacheRepository paymentImportCacheRepository,
                                         IManagePaymentTransactionTypeService managePaymentTransactionTypeService
    ) {
        super(paymentEventPublisher);
        this.paymentService = paymentService;
        this.paymentDetailService = paymentDetailService;
        this.paymentImportCacheRepository = paymentImportCacheRepository;
        this.managePaymentTransactionTypeService = managePaymentTransactionTypeService;
    }

    @Override
    public void createValidators() {
        paymentImportDetailAmountValidator = new PaymentImportDetailAmountValidator(applicationEventPublisher);
        paymentDetailExistPaymentValidator=new PaymentDetailExistPaymentValidator(applicationEventPublisher,paymentService);
        paymentDetailsNoApplyDepositValidator= new PaymentDetailsNoApplyDepositValidator(applicationEventPublisher,managePaymentTransactionTypeService);
        paymentDetailBelongToSamePayment = new PaymentDetailBelongToSamePayment(applicationEventPublisher,paymentService);
        paymentImportAmountValidator = new PaymentImportAmountValidator(applicationEventPublisher, paymentDetailService, paymentImportCacheRepository);
        paymentTransactionIdValidator = new PaymentTransactionIdValidator(paymentDetailService, applicationEventPublisher);

    }

    @Override
    public boolean validate(PaymentDetailRow toValidate) {
        paymentDetailExistPaymentValidator.validate(toValidate,errorFieldList);
        this.validatePaymentAmount(toValidate);
        paymentDetailsNoApplyDepositValidator.validate(toValidate,errorFieldList);
        this.validateAsAntiToIncome(toValidate);
        this.validateAsExternalPaymentId(toValidate);

        if (this.hasErrors()) {
            PaymentImportDetailErrorEvent paymentImportErrorEvent =
                    new PaymentImportDetailErrorEvent(new PaymentDetailRowError(null,toValidate.getRowNumber(),
                            toValidate.getImportProcessId(), errorFieldList, toValidate));
            this.sendErrorEvent(paymentImportErrorEvent);
        }
        boolean result = !this.hasErrors();
        this.clearErrors();
        return result;

    }

    private void validatePaymentAmount(PaymentDetailRow toValidate){
        if (Objects.isNull(toValidate.getAnti())){
            paymentImportDetailAmountValidator.validate(toValidate,errorFieldList);
        }
    }
    private void validateAsAntiToIncome(PaymentDetailRow toValidate){
        if (Objects.nonNull(toValidate.getAnti()) && toValidate.getAnti()>0){
            AntiToIncomeRow antiToIncomeRow = new AntiToIncomeRow();
            antiToIncomeRow.setTransactionId(toValidate.getAnti());
            antiToIncomeRow.setAmount(toValidate.getBalance());
            antiToIncomeRow.setRemarks(toValidate.getRemarks());
            antiToIncomeRow.setImportProcessId(toValidate.getImportProcessId());
            paymentTransactionIdValidator.validate(antiToIncomeRow, errorFieldList);
            paymentImportAmountValidator.validate(antiToIncomeRow, errorFieldList);
        }

    }

    private void validateAsExternalPaymentId(PaymentDetailRow toValidate){
        if (Objects.nonNull(toValidate.getExternalPaymentId())){
            paymentDetailBelongToSamePayment.validate(toValidate,errorFieldList);
        }
    }


}
