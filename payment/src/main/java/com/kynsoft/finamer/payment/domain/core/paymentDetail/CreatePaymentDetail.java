package com.kynsoft.finamer.payment.domain.core.paymentDetail;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.payment.domain.core.paymentStatusHistory.PaymentStatusHistory;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckAmountGreaterThanZeroStrictlyRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckAmountIfGreaterThanPaymentBalanceRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentDetailAmountGreaterThanZeroRule;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CreatePaymentDetail {

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
    private PaymentStatusHistoryDto paymentStatusHistory;

    @Getter
    private boolean isPaymentApplied;

    public enum PaymentTransactionTypeCode {
        CASH,
        DEPOSIT,
        OTHER_DEDUCTIONS;

        public static PaymentTransactionTypeCode from(ManagePaymentTransactionTypeDto paymentTransactionType) {
            if (paymentTransactionType.getCash()) {
                return CASH;
            }
            if (paymentTransactionType.getDeposit()) {
                return DEPOSIT;
            }
            return OTHER_DEDUCTIONS;
        }
    }

    public CreatePaymentDetail(PaymentDto payment,
                               Double amount,
                               OffsetDateTime transactionDate,
                               ManageEmployeeDto employee,
                               String remark,
                               ManagePaymentTransactionTypeDto paymentTransactionType,
                               ManagePaymentStatusDto paymentStatus){
        this.payment = payment;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.employee = employee;
        this.remark = remark;
        this.paymentTransactionType = paymentTransactionType;
        this.paymentStatus = paymentStatus;
    }

    public void createPaymentDetail(){
        this.validate();

        RulesChecker.checkRule(new CheckPaymentDetailAmountGreaterThanZeroRule(this.amount));

        this.detail = this.createPaymentDetail(this.payment,
                paymentTransactionType,
                this.amount,
                this.remark,
                this.transactionDate
        );

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
        }

        detail.setEffectiveDate(this.transactionDate);
    }

    private void validate() {
        if (payment == null) throw new IllegalArgumentException("Payment must not be null");
        if (transactionDate == null) throw new IllegalArgumentException("transactionDate must not be null");
        if (employee == null) throw new IllegalArgumentException("employee must not be null");
        if (remark == null) throw new IllegalArgumentException("remark must not be null");
        if (paymentTransactionType == null) throw new IllegalArgumentException("paymentTransactionType must not be null");
        if (paymentStatus == null) throw new IllegalArgumentException("paymentStatus must not be null");
    }

    private PaymentDetailDto createPaymentDetail(PaymentDto paymentDto,
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
        if(remark.isBlank()){
            return paymentTransactionType.getDefaultRemark();
        }
        return remark;
    }

    private void updatePaymentTypeCash(PaymentDto paymentDto, Double amount){
        paymentDto.setIdentified(BankerRounding.round(paymentDto.getIdentified() + amount));
        paymentDto.setNotIdentified(BankerRounding.round(paymentDto.getNotIdentified() - amount));
        paymentDto.setPaymentBalance(BankerRounding.round(paymentDto.getPaymentBalance() - amount));
        updatePaymentAsApplied();
    }

    private void updatePaymentTypeDeposit(PaymentDto paymentDto, Double amount){
        paymentDto.setNotApplied(BankerRounding.round(paymentDto.getNotApplied() - amount));
        paymentDto.setPaymentBalance(BankerRounding.round(paymentDto.getPaymentBalance() - amount));
        updatePaymentAsApplied();
    }

    private void updatePaymentTypeOtherDeductions(PaymentDto paymentDto, Double amount){
        paymentDto.setOtherDeductions(BankerRounding.round(paymentDto.getOtherDeductions() + amount));
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
}
