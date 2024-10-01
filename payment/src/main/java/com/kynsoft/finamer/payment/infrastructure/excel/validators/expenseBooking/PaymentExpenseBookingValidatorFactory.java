package com.kynsoft.finamer.payment.infrastructure.excel.validators.expenseBooking;

import com.kynsof.share.core.application.excel.validator.IValidatorFactory;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseBookingRow;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PaymentExpenseBookingValidatorFactory extends IValidatorFactory<PaymentExpenseBookingRow> {

    private  BookingFieldValidator bookingFieldValidator;
    private  BookingBalanceValidator bookingBalanceValidator;
    private PaymentExpenseBookingTransactionTypeValidation transactionTypeValidation;
    private RemarksValidator remarksValidator;
    private final IManageBookingService bookingService;

    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;


    public PaymentExpenseBookingValidatorFactory(ApplicationEventPublisher applicationEventPublisher,
                                                 IManageBookingService bookingService,
                                                 IManagePaymentTransactionTypeService paymentTransactionTypeService) {
        super(applicationEventPublisher);

        this.bookingService = bookingService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
    }
    @Override
    public void createValidators() {
            bookingBalanceValidator= new BookingBalanceValidator(applicationEventPublisher,bookingService);
            bookingFieldValidator =  new BookingFieldValidator(applicationEventPublisher,bookingService);
            transactionTypeValidation = new PaymentExpenseBookingTransactionTypeValidation(applicationEventPublisher,paymentTransactionTypeService);
            remarksValidator = new RemarksValidator(applicationEventPublisher,paymentTransactionTypeService);
    }
    @Override
    public boolean validate(PaymentExpenseBookingRow toValidate) {
        bookingFieldValidator.validate(toValidate,errorFieldList);
        bookingBalanceValidator.validate(toValidate,errorFieldList);
        transactionTypeValidation.validate(toValidate,errorFieldList);
        remarksValidator.validate(toValidate,errorFieldList);
//        if (this.hasErrors()) {
//            PaymentImportExpenseToBookingErrorEvent paymentImportErrorEvent =
//                    new PaymentImportExpenseToBookingErrorEvent(new PaymentExpenseBookingRowError(null, toValidate.getRowNumber(),
//                            toValidate.getImportProcessId(), errorFieldList, toValidate));
//            this.sendErrorEvent(paymentImportErrorEvent);
//        }
        boolean result = !this.hasErrors();
        this.clearErrors();
        return result;
    }
}
