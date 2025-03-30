package com.kynsoft.finamer.payment.infrastructure.services.helpers.entity;

import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.infrastructure.identity.ManageEmployee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentDetail {

    private UUID id;
    private ManageEmployeeDto employee;
    private Status status;
    private PaymentDto payment;
    private ManagePaymentTransactionTypeDto transactionType;
    private Double amount;
    private String remark;
    private ManageBookingDto booking;
    private Boolean applyPayment;

    public CreatePaymentDetail(ManageEmployeeDto employee,
                               Status status,
                               PaymentDto payment,
                               ManagePaymentTransactionTypeDto transactionType,
                               Double amount,
                               String remark,
                               ManageBookingDto booking,
                               Boolean applyPayment){
        this.id = UUID.randomUUID();
        this.employee = employee;
        this.status = status;
        this.payment = payment;
        this.transactionType = transactionType;
        this.amount = amount;
        this.remark = remark;
        this.booking = booking;
        this.applyPayment = applyPayment;
    }
}
