package com.kynsoft.finamer.payment.domain.core.applyPayment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.BankerRounding;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.core.paymentStatusHistory.PaymentStatusHistory;
import com.kynsoft.finamer.payment.domain.dto.*;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Objects;

public class ApplyPaymentDetail {

    private final PaymentDto payment;
    private final PaymentDetailDto paymentDetail;
    private final ManageBookingDto booking;
    private final OffsetDateTime transactionDate;
    private final Double amount;

    public ApplyPaymentDetail(PaymentDto payment,
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

    public void applyPayment(){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(this.paymentDetail, "id", "Payment Detail ID cannot be null."));

        updatePayment(this.payment, this.amount);
        updateBooking(this.booking, this.amount);
        updatePaymentDetail(paymentDetail, booking);
    }

    private void updatePayment(PaymentDto paymentDto, Double amount){
        paymentDto.setApplied(BankerRounding.round(paymentDto.getApplied() + amount));
        paymentDto.setNotApplied(BankerRounding.round(paymentDto.getNotApplied() - amount));
        this.payment.setApplyPayment(true);
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
