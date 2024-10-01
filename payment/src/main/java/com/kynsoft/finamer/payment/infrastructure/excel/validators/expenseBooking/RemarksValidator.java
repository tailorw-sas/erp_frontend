package com.kynsoft.finamer.payment.infrastructure.excel.validators.expenseBooking;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseBookingRow;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentTransactionType;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class RemarksValidator extends ExcelRuleValidator<PaymentExpenseBookingRow> {
    private final IManagePaymentTransactionTypeService  transactionTypeService;
    protected RemarksValidator(ApplicationEventPublisher applicationEventPublisher, IManagePaymentTransactionTypeService transactionTypeService) {
        super(applicationEventPublisher);
        this.transactionTypeService = transactionTypeService;
    }

    @Override
    public boolean validate(PaymentExpenseBookingRow obj, List<ErrorField> errorFieldList) {
        ManagePaymentTransactionTypeDto paymentTransactionType =transactionTypeService.findByCode(obj.getTransactionType());
        if(Objects.nonNull(obj.getRemarks()) && Objects.nonNull(paymentTransactionType) &&
                paymentTransactionType.getRemarkRequired() && obj.getRemarks().length()>paymentTransactionType.getMinNumberOfCharacter()){
            errorFieldList.add(new ErrorField("Remarks","Remarks is to long"));
            return false;
        }
        return true;
    }
}
