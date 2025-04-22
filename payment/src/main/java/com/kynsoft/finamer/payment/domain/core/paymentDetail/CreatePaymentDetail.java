package com.kynsoft.finamer.payment.domain.core.paymentDetail;

import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.core.paymentStatusHistory.PaymentStatusHistory;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CreatePaymentDetail {

    private final UUID id;
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

    public CreatePaymentDetail(UUID id,
                               PaymentDto payment,
                               Double amount,
                               OffsetDateTime transactionDate,
                               ManageEmployeeDto employee,
                               String remark,
                               ManagePaymentTransactionTypeDto paymentTransactionType,
                               ManagePaymentStatusDto paymentStatus){
        this.id = id;
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

        this.detail = this.createPaymentDetail(id,
                this.payment,
                paymentTransactionType,
                this.amount,
                this.remark,
                this.transactionDate
        );

        this.updatePayment(this.payment, this.amount);

        detail.setEffectiveDate(this.transactionDate);

        if(this.payment.getPaymentBalance() == 0 && this.payment.getDepositBalance() == 0){
            this.payment.setPaymentStatus(this.paymentStatus);
            this.paymentStatusHistory = createPaymentStatusHistory();
            this.isPaymentApplied = true;
        }
    }

    private void validate() {
        if (id == null) throw new IllegalArgumentException("Id must not be null");
        if (payment == null) throw new IllegalArgumentException("Payment must not be null");
        if (transactionDate == null) throw new IllegalArgumentException("transactionDate must not be null");
        if (employee == null) throw new IllegalArgumentException("employee must not be null");
        if (remark == null) throw new IllegalArgumentException("remark must not be null");
        if (paymentTransactionType == null) throw new IllegalArgumentException("paymentTransactionType must not be null");
        if (paymentStatus == null) throw new IllegalArgumentException("paymentStatus must not be null");
    }

    private PaymentDetailDto createPaymentDetail(UUID id,
                                                 PaymentDto paymentDto,
                                                 ManagePaymentTransactionTypeDto paymentTransactionTypeDto,
                                                 Double amount,
                                                 String remark,
                                                 OffsetDateTime transactionDate){
        return new PaymentDetailDto(
                id,
                Status.ACTIVE,
                paymentDto,
                paymentTransactionTypeDto,
                amount,
                remark,
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

    private void updatePayment(PaymentDto paymentDto, Double amount){
        ConsumerUpdate updatePayment = new ConsumerUpdate();

        UpdateIfNotNull.updateDouble(paymentDto::setIdentified, paymentDto.getIdentified() + amount, updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentDto::setNotIdentified, paymentDto.getNotIdentified() - amount, updatePayment::setUpdate);

        //Suma de trx tipo check Cash + Check Apply Deposit  en el Manage Payment Transaction Type
        UpdateIfNotNull.updateDouble(paymentDto::setApplied, paymentDto.getApplied() + amount, updatePayment::setUpdate);

        //Las transacciones de tipo Cash se restan al Payment Balance.
        UpdateIfNotNull.updateDouble(paymentDto::setPaymentBalance, paymentDto.getPaymentBalance() - amount, updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentDto::setNotApplied, paymentDto.getNotApplied() - amount, updatePayment::setUpdate);
    }

    private PaymentStatusHistoryDto createPaymentStatusHistory(){
        PaymentStatusHistory paymentAttachmentStatusHistory = new PaymentStatusHistory(this.employee, this.payment);
        paymentAttachmentStatusHistory.create();
        return paymentAttachmentStatusHistory.getPaymentStatusHistory();
    }
}
