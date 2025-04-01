package com.kynsoft.finamer.payment.infrastructure.services.helpers.entity.paymentStatusHistory;

import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import lombok.Getter;

import java.util.UUID;

public class PaymentStatusHistory {

    private final ManageEmployeeDto employee;
    private final PaymentDto payment;

    @Getter
    private PaymentStatusHistoryDto paymentStatusHistory;

    public PaymentStatusHistory(ManageEmployeeDto employee,
                                PaymentDto payment){
        this.employee = employee;
        this.payment = payment;
        this.paymentStatusHistory = new PaymentStatusHistoryDto();
    }

    public void create(){
        this.paymentStatusHistory.setId(UUID.randomUUID());
        this.paymentStatusHistory.setDescription("Update Payment.");
        this.paymentStatusHistory.setEmployee(this.employee);
        this.paymentStatusHistory.setPayment(this.payment);
        this.paymentStatusHistory.setStatus(this.payment.getPaymentStatus().getCode() + "-" + this.payment.getPaymentStatus().getName());
    }
}
