package com.kynsoft.finamer.payment.domain.core.applyPayment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.BankerRounding;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.core.paymentStatusHistory.PaymentStatusHistory;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Objects;

public class ApplyPayment {

    private final PaymentDto payment;
    private final PaymentDetailDto paymentDetail;
    private final ManageBookingDto booking;

    @Getter
    private PaymentStatusHistoryDto paymentStatusHistory;

    @Getter
    private boolean isPaymentApplied;

    private final OffsetDateTime transactionDate;
    private final ManageEmployeeDto employee;
    private final ManagePaymentStatusDto paymentStatus;
    private final Double amount;

    public ApplyPayment(PaymentDto payment,
                        PaymentDetailDto paymentDetail,
                        ManageBookingDto booking,
                        OffsetDateTime transactionDate,
                        ManageEmployeeDto employee,
                        ManagePaymentStatusDto paymentStatus,
                        Double amount){
        this.booking = booking;
        this.payment = payment;
        this.paymentDetail = paymentDetail;
        this.transactionDate = transactionDate;
        this.employee = employee;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
    }

    public void applyPayment(){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(this.paymentDetail, "id", "Payment Detail ID cannot be null."));

        updatePayment(this.payment, this.amount);

        if(Objects.nonNull(this.booking)){
            updateBooking(this.booking, this.amount);
            this.paymentDetail.setManageBooking(this.booking);
            this.paymentDetail.setApplyPayment(true);
            this.paymentDetail.setAppliedAt(OffsetDateTime.now());
            this.payment.setApplyPayment(true);
        }

        this.paymentDetail.setEffectiveDate(this.transactionDate);

        if(this.payment.getPaymentBalance() == 0 && this.payment.getDepositBalance() == 0){
            this.payment.setPaymentStatus(this.paymentStatus);
            this.paymentStatusHistory = createPaymentStatusHistory();
            this.isPaymentApplied = true;
        }
    }

    private PaymentStatusHistoryDto createPaymentStatusHistory(){
        PaymentStatusHistory paymentAttachmentStatusHistory = new PaymentStatusHistory(this.employee, this.payment);
        paymentAttachmentStatusHistory.create();
        return paymentAttachmentStatusHistory.getPaymentStatusHistory();
    }

    private void updatePayment(PaymentDto paymentDto, Double amount){
        ConsumerUpdate updatePayment = new ConsumerUpdate();

        UpdateIfNotNull.updateDouble(paymentDto::setIdentified, BankerRounding.round(paymentDto.getIdentified() + amount), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentDto::setNotIdentified, BankerRounding.round(paymentDto.getNotIdentified() - amount), updatePayment::setUpdate);

        //Suma de trx tipo check Cash + Check Apply Deposit  en el Manage Payment Transaction Type
        UpdateIfNotNull.updateDouble(paymentDto::setApplied, BankerRounding.round(paymentDto.getApplied() + amount), updatePayment::setUpdate);

        //Las transacciones de tipo Cash se restan al Payment Balance.
        UpdateIfNotNull.updateDouble(paymentDto::setPaymentBalance, BankerRounding.round(paymentDto.getPaymentBalance() - amount), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentDto::setNotApplied, BankerRounding.round(paymentDto.getNotApplied() - amount), updatePayment::setUpdate);
    }

    private void updateBooking(ManageBookingDto booking, Double amount){
        this.booking.setAmountBalance(BankerRounding.round(this.booking.getAmountBalance() - amount));
    }
}
