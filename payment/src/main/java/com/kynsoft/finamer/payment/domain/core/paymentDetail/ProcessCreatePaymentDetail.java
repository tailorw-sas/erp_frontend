package com.kynsoft.finamer.payment.domain.core.paymentDetail;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.payment.domain.core.enums.PaymentTransactionTypeCode;
import com.kynsoft.finamer.payment.domain.core.paymentStatusHistory.PaymentStatusHistory;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckAmountGreaterThanZeroStrictlyRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckAmountIfGreaterThanPaymentBalanceRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckGreaterThanOrEqualToTheTransactionAmountRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentDetailAmountGreaterThanZeroRule;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ProcessCreatePaymentDetail {

    private final PaymentDto payment;
    private final Double amount;
    private final OffsetDateTime transactionDate;
    private final ManageEmployeeDto employee;
    private final String remark;
    private final ManagePaymentTransactionTypeDto paymentTransactionType;
    private final ManagePaymentStatusDto paymentStatus;


    @Getter
    private PaymentDetailDto detail;

    @Getter
    private final PaymentDetailDto parentDetail;

    @Getter
    private PaymentStatusHistoryDto paymentStatusHistory;

    @Getter
    private boolean isPaymentApplied;

    public ProcessCreatePaymentDetail(PaymentDto payment,
                                      Double amount,
                                      OffsetDateTime transactionDate,
                                      ManageEmployeeDto employee,
                                      String remark,
                                      ManagePaymentTransactionTypeDto paymentTransactionType,
                                      ManagePaymentStatusDto paymentStatus,
                                      PaymentDetailDto parentDetail){
        this.payment = payment;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.employee = employee;
        this.remark = remark;
        this.paymentTransactionType = paymentTransactionType;
        this.paymentStatus = paymentStatus;
        this.parentDetail = parentDetail;
    }

    public void process(){
        this.validate();

        RulesChecker.checkRule(new CheckPaymentDetailAmountGreaterThanZeroRule(this.amount));

        this.detail = this.createPaymentDetailEntity(this.payment, paymentTransactionType, this.amount, this.remark, this.transactionDate);
        this.addDetailToPayment(this.payment, this.detail);

        switch(PaymentTransactionTypeCode.from(this.paymentTransactionType)){
            case CASH -> {
                RulesChecker.checkRule(new CheckAmountGreaterThanZeroStrictlyRule(this.amount, this.payment.getPaymentBalance()));
                this.updatePaymentTypeCash(this.payment, this.amount);
            }
            case DEPOSIT -> {
                RulesChecker.checkRule(new CheckAmountIfGreaterThanPaymentBalanceRule(this.amount, this.payment.getPaymentBalance(), this.payment.getDepositAmount()));
                this.updatePaymentTypeDeposit(this.payment, this.amount);
                this.detail.setAmount(this.amount * -1);
                this.detail.setApplyDepositValue(this.amount);
            }
            case OTHER_DEDUCTIONS -> {
                this.updatePaymentTypeOtherDeductions(this.payment, this.amount);
            }
            case APPLY_DEPOSIT -> {
                RulesChecker.checkRule(new CheckGreaterThanOrEqualToTheTransactionAmountRule(this.amount, parentDetail.getApplyDepositValue()));//TODO Verificar
                this.updatePaymentTypeApplyDeposit(this.payment, this.amount);
                this.updateDetailWhenApplyDeposit(this.detail, this.parentDetail);
                this.updateParentDetailWhenApplyDeposit(this.detail, this.parentDetail, this.amount);
            }
        }
    }

    private void validate() {
        if (payment == null) throw new IllegalArgumentException("Payment must not be null");
        if (transactionDate == null) throw new IllegalArgumentException("transactionDate must not be null");
        if (employee == null) throw new IllegalArgumentException("employee must not be null");
        if (paymentTransactionType == null) throw new IllegalArgumentException("paymentTransactionType must not be null");
        if (paymentStatus == null && !paymentTransactionType.getCash() && !paymentTransactionType.getDeposit() && !paymentTransactionType.getApplyDeposit()) throw new IllegalArgumentException("paymentStatus must not be null");
    }

    private PaymentDetailDto createPaymentDetailEntity(PaymentDto paymentDto,
                                                 ManagePaymentTransactionTypeDto paymentTransactionTypeDto,
                                                 Double amount,
                                                 String remark,
                                                 OffsetDateTime transactionDate){
        return new PaymentDetailDto(
                UUID.randomUUID(),
                Status.ACTIVE,
                paymentDto,
                paymentTransactionTypeDto,
                amount,
                this.getRemark(remark, paymentTransactionType),
                null,
                null,
                null,
                transactionDate,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }

    private String getRemark(String remark, ManagePaymentTransactionTypeDto paymentTransactionType){
        if(Objects.isNull(remark) || remark.isBlank()){
            return paymentTransactionType.getDefaultRemark();
        }
        return remark;
    }

    private void updatePaymentTypeCash(PaymentDto paymentDto, Double amount){
        paymentDto.setIdentified(BankerRounding.round(paymentDto.getIdentified() + amount));
        paymentDto.setNotIdentified(BankerRounding.round(paymentDto.getNotIdentified() - amount));
        paymentDto.setPaymentBalance(BankerRounding.round(paymentDto.getPaymentBalance() - amount));

        //Por definicion si se aplica con booking o no se debe afectar applied y noApplied
        paymentDto.setApplied(BankerRounding.round(paymentDto.getApplied() + amount));
        paymentDto.setNotApplied(BankerRounding.round(paymentDto.getNotApplied() - amount));
        updatePaymentAsApplied();
    }

    private void updatePaymentTypeDeposit(PaymentDto paymentDto, Double amount){
        paymentDto.setPaymentBalance(BankerRounding.round(paymentDto.getPaymentBalance() - amount));
        paymentDto.setDepositAmount(BankerRounding.round(paymentDto.getDepositAmount() + amount));
        paymentDto.setDepositBalance(BankerRounding.round(paymentDto.getDepositBalance() + amount));
        paymentDto.setNotApplied(BankerRounding.round(paymentDto.getNotApplied() - amount));

        updatePaymentAsApplied();
    }

    private void updatePaymentTypeOtherDeductions(PaymentDto paymentDto, Double amount){
        paymentDto.setOtherDeductions(BankerRounding.round(paymentDto.getOtherDeductions() + amount));
    }

    private void updatePaymentTypeApplyDeposit(PaymentDto paymentDto, Double amount){
        paymentDto.setDepositBalance(BankerRounding.round(paymentDto.getDepositBalance() - amount));
        paymentDto.setApplied(BankerRounding.round(paymentDto.getApplied() + amount));
        paymentDto.setIdentified(BankerRounding.round(paymentDto.getIdentified() + amount));
        paymentDto.setNotIdentified(BankerRounding.round(paymentDto.getPaymentAmount() - paymentDto.getIdentified()));
        paymentDto.setNotApplied(BankerRounding.round(paymentDto.getNotApplied() - amount));

        updatePaymentAsApplied();
    }

    private void updateParentDetailWhenApplyDeposit(PaymentDetailDto detail, PaymentDetailDto parentDetail, Double amount){
        List<PaymentDetailDto> paymentDetails = new ArrayList<>();
        if(Objects.nonNull(parentDetail.getPaymentDetails())){
            paymentDetails.addAll(parentDetail.getPaymentDetails());
        }
        paymentDetails.add(detail);
        parentDetail.setPaymentDetails(paymentDetails);
        parentDetail.setApplyDepositValue(BankerRounding.round(parentDetail.getApplyDepositValue() - amount));

    }

    private void updateDetailWhenApplyDeposit(PaymentDetailDto detail, PaymentDetailDto parentDetail){
        detail.setParentId(parentDetail.getPaymentDetailId());
    }

    private PaymentStatusHistoryDto createPaymentStatusHistory(){
        PaymentStatusHistory paymentAttachmentStatusHistory = new PaymentStatusHistory(this.employee, this.payment);
        paymentAttachmentStatusHistory.create();
        return paymentAttachmentStatusHistory.getPaymentStatusHistory();
    }

    private void updatePaymentAsApplied(){
        if(this.payment.getPaymentBalance() == 0 && this.payment.getDepositBalance() == 0){
            this.payment.setPaymentStatus(this.paymentStatus);
            this.paymentStatusHistory = createPaymentStatusHistory();
            this.isPaymentApplied = true;
        }
    }

    private void addDetailToPayment(PaymentDto payment, PaymentDetailDto paymentDetail){
        List<PaymentDetailDto> currentDetails = new ArrayList<>();
        if(payment.getPaymentDetails() != null){
            currentDetails.addAll(payment.getPaymentDetails());
        }
        currentDetails.add(paymentDetail);
        payment.setPaymentDetails(currentDetails);
    }
}
