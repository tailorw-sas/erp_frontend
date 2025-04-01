package com.kynsoft.finamer.payment.infrastructure.services.helpers.entity.applyPayment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.rules.applyOtherDeductions.CheckAmountGreaterThanZeroStrictlyApplyOtherDeductionsRule;
import lombok.Getter;

import java.time.OffsetDateTime;

public class ApplyOtherDeduction {

    @Getter
    private PaymentDto payment;

    @Getter
    private PaymentDetailDto paymentDetail;
    @Getter
    private PaymentStatusHistoryDto paymentStatusHistory;

    private final ManageBookingDto booking;
    private final ManagePaymentTransactionTypeDto paymentTransactionType;
    private final OffsetDateTime transactionDate;
    private final ManageEmployeeDto employee;
    private final ManagePaymentStatusDto paymentStatus;


    public ApplyOtherDeduction(PaymentDto payment,
                               PaymentDetailDto paymentDetail,
                               ManageBookingDto booking,
                               ManagePaymentTransactionTypeDto paymentTransactionType,
                               OffsetDateTime transactionDate,
                               ManageEmployeeDto employee,
                               ManagePaymentStatusDto paymentStatus,
                               PaymentStatusHistoryDto paymentStatusHistory){
        this.payment = payment;
        this.paymentDetail = paymentDetail;
        this.booking = booking;
        this.paymentTransactionType = paymentTransactionType;
        this.transactionDate = transactionDate;
        this.employee = employee;
        this.paymentStatus = paymentStatus;
        this.paymentStatusHistory = paymentStatusHistory;
    }

    public void applyOtherDeduction(){
        RulesChecker.checkRule(new CheckAmountGreaterThanZeroStrictlyApplyOtherDeductionsRule(this.paymentDetail.getAmount(), this.booking.getAmountBalance()));
        this.payment.setOtherDeductions(BankerRounding.round(this.payment.getOtherDeductions() + this.paymentDetail.getAmount()));

        String remark = this.paymentDetail.getRemark();
        if(this.paymentDetail.getRemark().isBlank()){
            remark = this.paymentTransactionType.getDefaultRemark();
        }

        this.paymentDetail.setRemark(remark);
        this.payment.setApplyPayment(true);

        this.applyPayment();
    }

    private void applyPayment(){
        ApplyPayment apply = new ApplyPayment(this.payment,
                this.paymentDetail,
                this.booking,
                this.transactionDate,
                this.employee,
                this.paymentStatus);
        apply.applyPayment();
        this.payment = apply.getPayment();
        this.paymentDetail = apply.getPaymentDetail();
        if(apply.isApplied()){
            this.paymentStatusHistory = apply.getPaymentStatusHistory();
        }
    }
}
