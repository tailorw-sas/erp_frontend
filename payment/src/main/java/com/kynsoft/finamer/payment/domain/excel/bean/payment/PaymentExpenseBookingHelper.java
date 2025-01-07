package com.kynsoft.finamer.payment.domain.excel.bean.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PaymentExpenseBookingHelper {
    private String transactionType;
    private Double balance;
    private String remark;
}
