package com.kynsoft.finamer.payment.infrastructure.services.helpers.entity.Deposit;

import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class Deposit {

    private final PaymentDto payment;
    private final Double amount;
    private final String remark;
    private final ManagePaymentTransactionTypeDto paymentTransactionType;
    private final OffsetDateTime transactionDate;

    @Getter
    private PaymentDetailDto paymentDetail;

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
        this.paymentDetail.setTransactionType(this.paymentTransactionType); //this.paymentTransactionTypeService.findByDeposit(),
        this.paymentDetail.setAmount(this.amount * -1);
        this.paymentDetail.setRemark(this.remark);
        this.paymentDetail.setTransactionDate(this.transactionDate);
        this.paymentDetail.setApplyPayment(false);
        this.paymentDetail.setApplyDepositValue(paymentDetail.getAmount() * -1);
        //TODO Validar si es today o el close operation
        paymentDetail.setTransactionDate(OffsetDateTime.now(ZoneId.of("UTC")));
        paymentDetail.setCreateByCredit(false);
    }

}
