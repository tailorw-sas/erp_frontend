package com.kynsoft.finamer.payment.domain.core.deposit;

import com.kynsoft.finamer.payment.domain.core.paymentStatusHistory.PaymentStatusHistory;
import com.kynsoft.finamer.payment.domain.dto.*;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class ApplyDeposit {

    private final ManagePaymentTransactionTypeDto paymentTransactionType;
    private final ManagePaymentStatusDto paymentStatusApplied;
    private final OffsetDateTime transactionDate;
    private final ManageEmployeeDto employee;

    private PaymentDetailDto depositPaymentDetail;
    private PaymentDto payment;
    private PaymentDetailDto paymentDetail;
    private Double amount;
    private ManageBookingDto booking;
    private PaymentStatusHistoryDto paymentStatusHistory;
    private boolean paymentApplied;
    private String errorMessage;

    public ApplyDeposit(ManagePaymentTransactionTypeDto paymentTransactionType,
                        PaymentDetailDto depositPaymentDetail,
                        PaymentDto payment,
                        PaymentDetailDto paymentDetail,
                        Double amount,
                        ManagePaymentStatusDto paymentStatusApplied,
                        ManageBookingDto booking,
                        OffsetDateTime transactionDate,
                        ManageEmployeeDto employee
                        ){
        this.paymentTransactionType = paymentTransactionType;
        this.depositPaymentDetail = depositPaymentDetail;
        this.payment = payment;
        this.paymentDetail = paymentDetail;
        this.amount = amount;
        this.paymentStatusApplied = paymentStatusApplied;
        this.booking = booking;
        this.transactionDate = transactionDate;
        this.employee = employee;
    }

    public Boolean apply(){
        if (paymentStatusApplied == null || !paymentStatusApplied.getApplied()) {
            this.errorMessage = "ManagePaymentStatusDto is not valid for apply payment";
            return false;
        }
        if (amount == null || amount <= 0) {
            this.errorMessage = "Amount must be greater than zero";
            return false;
        }
        if (payment == null || booking == null || paymentDetail == null) {
            this.errorMessage = "Missing required data for deposit application";
            return false;
        }

        updatePayment(this.payment, this.amount, this.paymentStatusApplied, this.paymentStatusHistory, this.employee);
        updateBooking(this.booking, this.amount);
        updatePaymentDetail(this.paymentDetail, this.depositPaymentDetail, this.booking, this.transactionDate);
        updateParentPaymentDetail(this.depositPaymentDetail, this.paymentDetail, this.amount);

        return true;
    }

    private void updatePayment(PaymentDto payment,
                               Double amount,
                               ManagePaymentStatusDto paymentStatusApplied,
                               PaymentStatusHistoryDto paymentStatusHistory,
                               ManageEmployeeDto employee
                               ){
        payment.setDepositBalance(payment.getDepositBalance() - amount);
        payment.setApplied(payment.getApplied() + amount);
        payment.setIdentified(payment.getIdentified() + amount);
        payment.setNotIdentified(payment.getPaymentAmount() - payment.getIdentified());
        payment.setNotApplied(payment.getPaymentAmount() - payment.getIdentified());

        if (payment.getPaymentBalance() == 0 && payment.getDepositBalance() == 0) {
            payment.setPaymentStatus(paymentStatusApplied);
            createPaymentStatusHistory(payment, employee, paymentStatusHistory);
            this.paymentApplied = true;
        }

        payment.setApplyPayment(true);
    }

    private void updateBooking(ManageBookingDto booking, Double amount){
        booking.setAmountBalance(booking.getAmountBalance() - amount);
    }

    private void updatePaymentDetail(PaymentDetailDto paymentDetail,
                                     PaymentDetailDto parentPaymentDetail,
                                     ManageBookingDto booking,
                                     OffsetDateTime transactionDate){
        paymentDetail.setParentId(parentPaymentDetail.getParentId());
        paymentDetail.setManageBooking(booking);
        paymentDetail.setApplyPayment(true);
        paymentDetail.setAppliedAt(OffsetDateTime.now());
        paymentDetail.setEffectiveDate(transactionDate);
    }

    private void updateParentPaymentDetail(PaymentDetailDto depositPaymentDetail, PaymentDetailDto paymentDetail, Double amount){
        List<PaymentDetailDto> updatedList = new ArrayList<>(depositPaymentDetail.getPaymentDetails());
        updatedList.add(paymentDetail);
        depositPaymentDetail.setPaymentDetails(Collections.unmodifiableList(updatedList));
        depositPaymentDetail.setApplyDepositValue(depositPaymentDetail.getApplyDepositValue() - amount);
    }

    private void createPaymentStatusHistory(PaymentDto payment, ManageEmployeeDto employee, PaymentStatusHistoryDto paymentStatusHistory){
        PaymentStatusHistory paymentAttachmentStatusHistory = new PaymentStatusHistory(employee, payment);
        paymentAttachmentStatusHistory.create();
        paymentStatusHistory = paymentAttachmentStatusHistory.getPaymentStatusHistory();
    }
}
