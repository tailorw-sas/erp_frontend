package com.kynsoft.finamer.payment.domain.core.applyPayment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.rules.applyOtherDeductions.CheckAmountGreaterThanZeroStrictlyApplyOtherDeductionsRule;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class ApplyOtherDeduction {

    @Getter
    private PaymentDto payment;

    @Getter
    private PaymentDetailDto paymentDetail;

    private final ManageBookingDto booking;
    private final ManagePaymentTransactionTypeDto paymentTransactionType;
    private final OffsetDateTime transactionDate;
    private final Double amount;


    public ApplyOtherDeduction(PaymentDto payment,
                               PaymentDetailDto paymentDetail,
                               ManageBookingDto booking,
                               ManagePaymentTransactionTypeDto paymentTransactionType,
                               OffsetDateTime transactionDate,
                               Double amount){
        this.payment = payment;
        this.paymentDetail = paymentDetail;
        this.booking = booking;
        this.paymentTransactionType = paymentTransactionType;
        this.transactionDate = transactionDate;
        this.amount = amount;
    }

    public void applyOtherDeduction(){
        RulesChecker.checkRule(new CheckAmountGreaterThanZeroStrictlyApplyOtherDeductionsRule(this.paymentDetail.getAmount(), this.amount));
        //TODO Validar que el booking exista
        //TODO Validar que el monto no sea mayor al balance del booking
        this.booking.setAmountBalance(BankerRounding.round(this.booking.getAmountBalance() - this.amount));

        this.payment.setOtherDeductions(BankerRounding.round(this.payment.getOtherDeductions() + this.amount));

        String remark = this.paymentDetail.getRemark();
        if(this.paymentDetail.getRemark().isBlank()){
            remark = this.paymentTransactionType.getDefaultRemark();
        }

        this.paymentDetail.setRemark(remark);
        this.paymentDetail.setManageBooking(this.booking);
        this.paymentDetail.setAppliedAt(OffsetDateTime.now());
        this.paymentDetail.setApplyPayment(true);
        this.paymentDetail.setEffectiveDate(this.transactionDate);
        this.payment.setApplyPayment(true);
    }
}
