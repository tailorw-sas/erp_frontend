package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.IValidatorFactory;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportError;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentDetailRowError;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.PaymentImportErrorEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.event.error.detail.PaymentImportDetailErrorEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PaymentDetailValidatorFactory extends IValidatorFactory<PaymentDetailRow> {

    private PaymentImportAmountValidator paymentImportAmountValidator;
    private PaymentDetailsNoApplyDepositValidator paymentDetailsNoApplyDepositValidator;

    private PaymentDetailExistPaymentValidator paymentDetailExistPaymentValidator;

    private PaymentDetailBelongToSamePayment paymentDetailBelongToSamePayment;

    private final IPaymentService paymentService;

    private final IManagePaymentTransactionTypeService managePaymentTransactionTypeService;


    public PaymentDetailValidatorFactory(ApplicationEventPublisher paymentEventPublisher, IPaymentService paymentService,
                                         IManagePaymentTransactionTypeService managePaymentTransactionTypeService
    ) {
        super(paymentEventPublisher);
        this.paymentService = paymentService;
        this.managePaymentTransactionTypeService = managePaymentTransactionTypeService;
    }

    @Override
    public void createValidators() {
        paymentImportAmountValidator = new PaymentImportAmountValidator(applicationEventPublisher);
        paymentDetailExistPaymentValidator=new PaymentDetailExistPaymentValidator(applicationEventPublisher,paymentService);
        paymentDetailsNoApplyDepositValidator= new PaymentDetailsNoApplyDepositValidator(applicationEventPublisher,managePaymentTransactionTypeService);
        paymentDetailBelongToSamePayment = new PaymentDetailBelongToSamePayment(applicationEventPublisher,paymentService);

    }

    @Override
    public boolean validate(PaymentDetailRow toValidate) {
        paymentDetailExistPaymentValidator.validate(toValidate,errorFieldList);
        paymentImportAmountValidator.validate(toValidate,errorFieldList);
        paymentDetailsNoApplyDepositValidator.validate(toValidate,errorFieldList);
        if (Objects.nonNull(toValidate.getExternalPaymentId())){
            paymentDetailBelongToSamePayment.validate(toValidate,errorFieldList);
        }
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


}
