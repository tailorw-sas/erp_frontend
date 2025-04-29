package com.kynsoft.finamer.payment.domain.core.applyPayment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckAmountGreaterThanZeroStrictlyAndLessBookingBalanceRule;

import java.time.OffsetDateTime;

public class ProcessApplyPaymentDetail {

    private final PaymentDto payment;
    private final PaymentDetailDto paymentDetail;
    private final ManageBookingDto booking;
    private final OffsetDateTime transactionDate;
    private final Double amount;

    public ProcessApplyPaymentDetail(PaymentDto payment,
                                     PaymentDetailDto paymentDetail,
                                     ManageBookingDto booking,
                                     OffsetDateTime transactionDate,
                                     Double amount){
        this.booking = booking;
        this.payment = payment;
        this.paymentDetail = paymentDetail;
        this.transactionDate = transactionDate;
        this.amount = amount;
    }

    public void process(){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(this.paymentDetail, "id", "Payment Detail ID cannot be null."));
        RulesChecker.checkRule(new CheckAmountGreaterThanZeroStrictlyAndLessBookingBalanceRule(this.amount, this.booking.getAmountBalance()));

        updatePayment(this.payment, this.amount);
        updateBooking(this.booking, this.amount);
        updatePaymentDetail(paymentDetail, booking);
    }

    private void updatePayment(PaymentDto paymentDto, Double amount){
        if(!this.payment.isApplyPayment()){
            this.payment.setApplyPayment(true);
        }
    }

    private void updateBooking(ManageBookingDto booking, Double amount){
        this.booking.setAmountBalance(BankerRounding.round(this.booking.getAmountBalance() - amount));
    }

    private void updatePaymentDetail(PaymentDetailDto paymentDetail, ManageBookingDto booking){
        this.paymentDetail.setManageBooking(this.booking);
        this.paymentDetail.setApplyPayment(true);
        this.paymentDetail.setAppliedAt(OffsetDateTime.now());
        this.paymentDetail.setEffectiveDate(this.transactionDate);
    }
}
