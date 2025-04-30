package com.kynsoft.finamer.payment.domain.core.paymentDetail;

import com.kynsoft.finamer.payment.domain.core.undoApplyPayment.UndoApplyPayment;
import com.kynsoft.finamer.payment.domain.dto.*;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProcessReverseDetail {

    private final PaymentDetailDto paymentDetail;
    private final PaymentDetailDto parent;
    private final PaymentDto payment;
    private final OffsetDateTime transactionDate;
    private final ManagePaymentStatusDto confirmedPaymentStatus;
    private final ManageEmployeeDto employee;
    private final PaymentStatusHistoryDto paymentStatusHistoryDto;
    private final ManageBookingDto booking;

    @Getter
    private Boolean isPaymentChangeStatus;

    @Getter
    private PaymentDetailDto paymentDetailClone;

    public ProcessReverseDetail(PaymentDetailDto paymentDetail,
                                PaymentDetailDto parent,
                                OffsetDateTime transactionDate,
                                PaymentDto payment,
                                ManagePaymentStatusDto confirmedPaymentStatus,
                                ManageEmployeeDto employee,
                                PaymentStatusHistoryDto paymentStatusHistoryDto,
                                ManageBookingDto booking){
        this.paymentDetail = paymentDetail;
        this.parent = parent;
        this.transactionDate = transactionDate;
        this.payment = payment;
        this.confirmedPaymentStatus = confirmedPaymentStatus;
        this.employee = employee;
        this.paymentStatusHistoryDto = paymentStatusHistoryDto;
        this.booking = booking;
    }

    public void process(){
        this.paymentDetailClone = this.createPaymentDetailClone();

        if (this.paymentDetail.getTransactionType().getApplyDeposit()) {
            paymentDetailClone.setReverseFrom(this.paymentDetail.getPaymentDetailId());
            paymentDetailClone.setParentId(this.paymentDetail.getParentId());
            this.addPaymentDetails(paymentDetailClone, this.parent);
            this.calculateReverseApplyDeposit(this.payment, this.paymentDetail);

        } else if (this.paymentDetail.getTransactionType().getCash()) {
            this.calculateReverseCash(this.payment, paymentDetailClone.getAmount());
        } else {
            this.calculateReverseOtherDeductions(this.payment, paymentDetailClone.getAmount());
        }

        this.paymentDetail.setReverseTransaction(true);

        this.undoApplyPayment(this.paymentDetail, this.booking);

        this.isPaymentChangeStatus = this.changeStatus(payment, this.paymentDetail, paymentStatusHistoryDto, this.employee);
    }

    private PaymentDetailDto createPaymentDetailClone(){
        PaymentDetailDto paymentDetailClone = new PaymentDetailDto(
                UUID.randomUUID(),
                this.paymentDetail.getStatus(),
                this.paymentDetail.getPayment(),
                this.paymentDetail.getTransactionType(),
                this.paymentDetail.getAmount() * -1,
                this.paymentDetail.getRemark(),
                null,
                null,
                null,
                this.transactionDate,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );

        paymentDetailClone.setManageBooking(this.paymentDetail.getManageBooking());
        paymentDetailClone.setReverseTransaction(true);
        paymentDetailClone.setReverseFromParentId(this.paymentDetail.getPaymentDetailId());
        return paymentDetailClone;
    }

    private void addPaymentDetails(PaymentDetailDto reverseFrom, PaymentDetailDto parent) {
        List<PaymentDetailDto> _paymentDetails = new ArrayList<>(parent.getPaymentDetails());
        _paymentDetails.add(reverseFrom);
        parent.setPaymentDetails(_paymentDetails);
        parent.setApplyDepositValue(parent.getApplyDepositValue() - reverseFrom.getAmount());
    }

    private void calculateReverseApplyDeposit(PaymentDto paymentDto, PaymentDetailDto newDetailDto) {
        paymentDto.setDepositBalance(paymentDto.getDepositBalance() + newDetailDto.getAmount());
        paymentDto.setApplied(paymentDto.getApplied() - newDetailDto.getAmount());
        paymentDto.setIdentified(paymentDto.getIdentified() - newDetailDto.getAmount());
        paymentDto.setNotIdentified(paymentDto.getNotIdentified() + newDetailDto.getAmount());
    }

    private void calculateReverseOtherDeductions(PaymentDto paymentDto, double amount) {
        paymentDto.setOtherDeductions(paymentDto.getOtherDeductions() + amount);
    }

    private void calculateReverseCash(PaymentDto paymentDto, double amount) {
        paymentDto.setIdentified(paymentDto.getIdentified() + amount);
        paymentDto.setNotIdentified(paymentDto.getNotIdentified() - amount);

        paymentDto.setApplied(paymentDto.getApplied() + amount);
        paymentDto.setNotApplied(paymentDto.getNotApplied() - amount);
        paymentDto.setPaymentBalance(paymentDto.getPaymentBalance() - amount);
    }

    private boolean changeStatus(PaymentDto payment, PaymentDetailDto paymentDetailDto, PaymentStatusHistoryDto paymentStatusHistory, ManageEmployeeDto employee) {
        if(paymentDetailDto.getTransactionType().getCash() || paymentDetailDto.getTransactionType().getApplyDeposit()){
            if (payment.getPaymentStatus().getApplied()) {
                payment.setPaymentStatus(this.confirmedPaymentStatus);
                this.createPaymentAttachmentStatusHistory(payment, paymentStatusHistory, employee);
                return true;
            }
        }

        return false;
    }

    private void createPaymentAttachmentStatusHistory(PaymentDto payment, PaymentStatusHistoryDto paymentStatusHistory, ManageEmployeeDto employee) {
        paymentStatusHistory.setId(UUID.randomUUID());
        paymentStatusHistory.setDescription("Update Payment.");
        paymentStatusHistory.setEmployee(employee);
        paymentStatusHistory.setPayment(payment);
        paymentStatusHistory.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());
    }

    private void undoApplyPayment(PaymentDetailDto paymentDetail, ManageBookingDto booking){
        UndoApplyPayment undoApplyPayment = new UndoApplyPayment(paymentDetail, booking);
        undoApplyPayment.undoApply();
    }
}
