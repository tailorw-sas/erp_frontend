package com.kynsoft.finamer.payment.infrastructure.excel.validators.expenseBooking;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseBookingRow;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentTransactionType;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class PaymentExpenseBookingTransactionTypeValidation extends ExcelRuleValidator<PaymentExpenseBookingRow> {

    private final IManagePaymentTransactionTypeService managePaymentTransactionTypeService;
    protected PaymentExpenseBookingTransactionTypeValidation(ApplicationEventPublisher applicationEventPublisher, IManagePaymentTransactionTypeService managePaymentTransactionTypeService) {
        super(applicationEventPublisher);
        this.managePaymentTransactionTypeService = managePaymentTransactionTypeService;
    }

    @Override
    public boolean validate(PaymentExpenseBookingRow obj, List<ErrorField> errorFieldList) {

        if (Objects.isNull(obj.getTransactionType())){
            errorFieldList.add(new ErrorField("transactionType","Transaction Type can't be empty"));
            return false;
        }

        return true;
    }
}
