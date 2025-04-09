package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.application.excel.validator.ICache;
import com.kynsof.share.core.application.excel.validator.IImportControl;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.ImportControl;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Objects;

public class PaymentDetailsNoApplyDepositValidator extends ExcelRuleValidator<PaymentDetailRow> {

    private final Cache cache;

    protected PaymentDetailsNoApplyDepositValidator(ApplicationEventPublisher applicationEventPublisher,
                                                    Cache cache) {
        super(applicationEventPublisher);
        this.cache = cache;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getTransactionType())) {
            errorFieldList.add(new ErrorField("Transaction type", "Transaction type can't be empty"));
            return false;
        }
        try {
            ManagePaymentTransactionTypeDto transactionTypeDto = this.cache.getManageTransactionTypeByCode(obj.getTransactionType().trim());

            if (Objects.isNull(transactionTypeDto)) {
                errorFieldList.add(new ErrorField("Transaction type", "Transaction type not exist"));
                return false;
            }
            if (Status.INACTIVE.name().equals(transactionTypeDto.getStatus())) {
                errorFieldList.add(new ErrorField("Transaction type", "Transaction type is inactive"));
                return false;
            }
            if (transactionTypeDto.getApplyDeposit() && Objects.isNull(obj.getAnti())) {
                errorFieldList.add(new ErrorField("Transaction type", "Anti column is mandatory for apply deposit transaction type"));
                return false;
            }
            if (Objects.nonNull(obj.getAnti()) && !transactionTypeDto.getApplyDeposit()) {
                errorFieldList.add(new ErrorField("Transaction type", "Transaction type must be a apply deposit"));
                return false;
            }
        } catch (Exception e) {
            errorFieldList.add(new ErrorField("Transaction type", "Transaction type not found."));
            return false;
        }

        return true;
    }
}
