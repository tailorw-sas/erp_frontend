package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class PaymentDetailsNoApplyDepositValidator extends ExcelRuleValidator<PaymentDetailRow> {

    private final IManagePaymentTransactionTypeService transactionTypeService;
    protected PaymentDetailsNoApplyDepositValidator(ApplicationEventPublisher applicationEventPublisher,
                                                    IManagePaymentTransactionTypeService transactionTypeService) {
        super(applicationEventPublisher);
        this.transactionTypeService = transactionTypeService;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getTransactionType())){
            errorFieldList.add(new ErrorField("Transaction type","Transaction type can't be empty"));
            return false;
        }
        ManagePaymentTransactionTypeDto transactionTypeDto = transactionTypeService.findByCode(obj.getTransactionType().trim());

        if (Objects.isNull(transactionTypeDto)){
            errorFieldList.add(new ErrorField("Transaction type","Transaction type not exist"));
            return false;
        }
        if (Status.INACTIVE.name().equals(transactionTypeDto.getStatus())){
            errorFieldList.add(new ErrorField("Transaction type","Transaction type is inactive"));
            return false;
        }
        if (transactionTypeDto.getApplyDeposit()){
            errorFieldList.add(new ErrorField("Transaction type","Transaction type can't be Apply Deposit"));
            return false;
        }
        if (Objects.nonNull(obj.getAnti()) && !transactionTypeDto.getDeposit()){
            errorFieldList.add(new ErrorField("Transaction type","Transaction type must be a deposit type"));
            return false;
        }


        return true;
    }
}
