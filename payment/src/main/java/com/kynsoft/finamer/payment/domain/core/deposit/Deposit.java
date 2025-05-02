package com.kynsoft.finamer.payment.domain.core.deposit;

import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class Deposit {

    @Getter
    private PaymentDto payment;

    @Getter
    private PaymentDetailDto paymentDetail;

    private final Double amount;
    private final String remark;
    private final ManagePaymentTransactionTypeDto paymentTransactionType;
    private final OffsetDateTime transactionDate;

    public Deposit(PaymentDto payment,
                   Double amount,
                   String remark,
                   ManagePaymentTransactionTypeDto paymentTransactionType,
                   OffsetDateTime transactionDate){
        this.payment = payment;
        this.amount = amount;
        this.remark = remark;
        this.paymentTransactionType = paymentTransactionType;
        this.transactionDate = transactionDate;
        this.paymentDetail = new PaymentDetailDto();
    }

    public void create(){
        this.paymentDetail.setId(UUID.randomUUID());
        this.paymentDetail.setStatus(Status.ACTIVE);
        this.paymentDetail.setPayment(this.payment);
        this.paymentDetail.setTransactionType(this.paymentTransactionType);
        this.paymentDetail.setAmount(this.amount * -1);
        this.paymentDetail.setRemark(this.remark);
        this.paymentDetail.setTransactionDate(this.transactionDate);
        this.paymentDetail.setApplyPayment(false);
        this.paymentDetail.setApplyDepositValue(paymentDetail.getAmount() * -1);
        this.paymentDetail.setCreateByCredit(false);

        this.calculate(this.payment, this.amount);
    }

    private void calculate(PaymentDto paymentDto, double amount) {
        paymentDto.setDepositAmount(paymentDto.getDepositAmount() + amount);
        paymentDto.setDepositBalance(paymentDto.getDepositBalance() + amount);
        paymentDto.setNotApplied(paymentDto.getNotApplied() - amount);
        paymentDto.setPaymentBalance(paymentDto.getPaymentBalance() - amount);
    }

}
