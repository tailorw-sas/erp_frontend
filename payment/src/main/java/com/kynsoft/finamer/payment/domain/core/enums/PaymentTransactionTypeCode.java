package com.kynsoft.finamer.payment.domain.core.enums;

import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;

public enum PaymentTransactionTypeCode {
    CASH,
    DEPOSIT,
    OTHER_DEDUCTIONS,
    APPLY_DEPOSIT;

    public static PaymentTransactionTypeCode from(ManagePaymentTransactionTypeDto paymentTransactionType) {
        if (paymentTransactionType.getCash()) {
            return CASH;
        }
        if (paymentTransactionType.getDeposit()) {
            return DEPOSIT;
        }
        if(paymentTransactionType.getApplyDeposit()){
            return APPLY_DEPOSIT;
        }
        return OTHER_DEDUCTIONS;
    }
}