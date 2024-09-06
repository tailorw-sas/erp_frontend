package com.kynsoft.finamer.payment.domain.dto;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentDetailDto {

    private UUID id;
    private Status status;
    private PaymentDto payment;
    private ManagePaymentTransactionTypeDto transactionType;
    private Double amount;
    private String remark;
    private List<PaymentDetailDto> children;
    private ManageBookingDto manageBooking;

    private Double bookingId;
    private String invoiceId;
    private OffsetDateTime transactionDate;
    private String firstName;
    private String lastName;
    private String reservation;
    private String couponNo;
    private Integer adults;
    private Integer childrens;
    private OffsetDateTime createdAt;
    private Long paymentDetailId;
    private Long parentId;
    private Double applyDepositValue;
    private Boolean applayPayment;

    public PaymentDetailDto(UUID id, Status status, PaymentDto payment, ManagePaymentTransactionTypeDto transactionType, Double amount, String remark, List<PaymentDetailDto> children, Double bookingId, String invoiceId, OffsetDateTime transactionDate, String firstName, String lastName, String reservation, String couponNo, Integer adults, Integer childrens, Boolean applayPayment) {
        this.id = id;
        this.status = status;
        this.payment = payment;
        this.transactionType = transactionType;
        this.amount = amount;
        this.remark = remark;
        this.children = children;
        this.bookingId = bookingId;
        this.invoiceId = invoiceId;
        this.transactionDate = transactionDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.reservation = reservation;
        this.couponNo = couponNo;
        this.adults = adults;
        this.childrens = childrens;
        this.applayPayment = applayPayment;
    }

}
