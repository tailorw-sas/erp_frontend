package com.kynsoft.finamer.payment.infrastructure.services.helpers.entity.applyPayment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.infrastructure.services.helpers.entity.paymentStatusHistory.PaymentStatusHistory;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public class ApplyPayment {

    @Getter
    private PaymentDto payment;

    @Getter
    private PaymentDetailDto paymentDetail;

    @Getter
    private PaymentStatusHistoryDto paymentStatusHistory;

    @Getter
    private ManageBookingDto booking;

    @Getter
    private boolean isApplied;

    private final OffsetDateTime transactionDate;
    private final ManageEmployeeDto employee;
    private final ManagePaymentStatusDto paymentStatus;

    public ApplyPayment(PaymentDto payment,
                        PaymentDetailDto paymentDetail,
                        ManageBookingDto booking,
                        OffsetDateTime transactionDate,
                        ManageEmployeeDto employee,
                        ManagePaymentStatusDto paymentStatus){
        this.booking = booking;
        this.payment = payment;
        this.paymentDetail = paymentDetail;
        this.transactionDate = transactionDate;
        this.employee = employee;
        this.paymentStatus = paymentStatus;
    }

    public void applyPayment(){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(this.booking, "id", "Booking ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(this.paymentDetail, "id", "Payment Detail ID cannot be null."));

        this.booking.setAmountBalance(BankerRounding.round(this.booking.getAmountBalance() - this.paymentDetail.getAmount()));
        this.paymentDetail.setManageBooking(this.booking);
        this.paymentDetail.setApplyPayment(true);
        this.paymentDetail.setAppliedAt(OffsetDateTime.now(ZoneId.of("UTC")));
        this.paymentDetail.setEffectiveDate(this.transactionDate);

        if(this.payment.getNotApplied() == 0 && this.payment.getDepositBalance() == 0 && !this.booking.getInvoice().getInvoiceType().equals(EInvoiceType.CREDIT)){
            this.payment.setPaymentStatus(this.paymentStatus);
            this.paymentStatusHistory = createPaymentStatusHistory();
            this.isApplied = true;
        }

        this.payment.setApplyPayment(true);
    }

    private PaymentStatusHistoryDto createPaymentStatusHistory(){
        PaymentStatusHistory paymentAttachmentStatusHistory = new PaymentStatusHistory(this.employee, this.payment);
        paymentAttachmentStatusHistory.create();
        return paymentAttachmentStatusHistory.getPaymentStatusHistory();
    }
}
