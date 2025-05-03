package com.kynsoft.finamer.payment.domain.core.paymentDetail;

import com.kynsoft.finamer.payment.domain.core.enums.PaymentTransactionTypeCode;
import com.kynsoft.finamer.payment.domain.core.undoApplyPayment.UndoApplyPayment;
import com.kynsoft.finamer.payment.domain.dto.*;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.kynsoft.finamer.payment.domain.core.enums.PaymentTransactionTypeCode.APPLY_DEPOSIT;
import static com.kynsoft.finamer.payment.domain.core.enums.PaymentTransactionTypeCode.CASH;

public class ProcessReversePaymentDetail {

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

    public ProcessReversePaymentDetail(PaymentDetailDto paymentDetail,
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

        switch(PaymentTransactionTypeCode.from(this.paymentDetail.getTransactionType())){
            case CASH -> {
                this.calculateReverseCash(this.payment, paymentDetailClone.getAmount());
            }
            case APPLY_DEPOSIT -> {
                paymentDetailClone.setReverseFrom(this.paymentDetail.getPaymentDetailId());
                paymentDetailClone.setParentId(this.paymentDetail.getParentId());
                this.addPaymentDetails(paymentDetailClone, this.parent);
                this.calculateReverseApplyDeposit(this.payment, this.paymentDetail);
            }
            case OTHER_DEDUCTIONS -> {
                this.calculateReverseOtherDeductions(this.payment, paymentDetailClone.getAmount());
            }
        }

        this.paymentDetail.setReverseTransaction(true);

        this.undoApplyPayment(this.paymentDetail, this.booking);

        this.isPaymentChangeStatus = this.changeStatus(payment,
                this.paymentDetail,
                paymentStatusHistoryDto,
                this.employee,
                PaymentTransactionTypeCode.from(this.paymentDetail.getTransactionType())
        );
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

    private void calculateReverseCash(PaymentDto paymentDto, double amount) {
        paymentDto.setIdentified(paymentDto.getIdentified() + amount);
        paymentDto.setNotIdentified(paymentDto.getNotIdentified() - amount);

        paymentDto.setApplied(paymentDto.getApplied() + amount);
        paymentDto.setNotApplied(paymentDto.getNotApplied() - amount);
        paymentDto.setPaymentBalance(paymentDto.getPaymentBalance() - amount);
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

    private void addPaymentDetails(PaymentDetailDto reverseFrom, PaymentDetailDto parent) {
        List<PaymentDetailDto> _paymentDetails = new ArrayList<>(parent.getPaymentDetails());
        _paymentDetails.add(reverseFrom);
        parent.setPaymentDetails(_paymentDetails);
        parent.setApplyDepositValue(parent.getApplyDepositValue() - reverseFrom.getAmount());
    }

    private boolean changeStatus(PaymentDto payment,
                                 PaymentDetailDto paymentDetailDto,
                                 PaymentStatusHistoryDto paymentStatusHistory,
                                 ManageEmployeeDto employee,
                                 PaymentTransactionTypeCode paymentTransactionTypeCode) {
        if((paymentTransactionTypeCode.equals(CASH) || paymentTransactionTypeCode.equals(APPLY_DEPOSIT)
                && payment.getPaymentStatus().getApplied())){
            payment.setPaymentStatus(this.confirmedPaymentStatus);
            this.createPaymentAttachmentStatusHistory(payment, paymentStatusHistory, employee);
            return true;
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
        undoApplyPayment.process();
    }
}
