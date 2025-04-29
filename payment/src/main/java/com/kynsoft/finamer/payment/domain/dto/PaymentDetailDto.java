package com.kynsoft.finamer.payment.domain.dto;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
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
    private List<PaymentDetailDto> paymentDetails;
    private ManageBookingDto manageBooking;

    private Double bookingId;
    private String invoiceId;
    private OffsetDateTime transactionDate;
    private String firstName;
    private String lastName;
    private String reservation;
    private String couponNo;
    private Integer adults;
    private Integer children;
    private OffsetDateTime createdAt;
    private Long paymentDetailId;
    private Long parentId;
    private Double applyDepositValue;
    private Long reverseFrom;
    private Long reverseFromParentId;//Esta variable es para poder controlar el undo luego de realizar un reverse.
    private boolean reverseTransaction;
    private boolean createByCredit;//Para identificar cuando un Details fue creado por un proceso automatico de la HU154.
    private boolean canceledTransaction;
    private Boolean applyPayment;
    private OffsetDateTime appliedAt;
    private OffsetDateTime effectiveDate;

    public PaymentDetailDto(UUID id, Status status, PaymentDto payment, ManagePaymentTransactionTypeDto transactionType,
                            Double amount, String remark, List<PaymentDetailDto> paymentDetails, Double bookingId,
                            String invoiceId, OffsetDateTime transactionDate, String firstName, String lastName,
                            String reservation, String couponNo, Integer adults, Integer children, Boolean applyPayment
                            ) {
        this.id = id;
        this.status = status;
        this.payment = payment;
        this.transactionType = transactionType;
        this.amount = amount;
        this.remark = remark;
        this.paymentDetails = paymentDetails;
        this.bookingId = bookingId;
        this.invoiceId = invoiceId;
        this.transactionDate = transactionDate;
        this.firstName = firstName;
        this.lastName = lastName;
        this.reservation = reservation;
        this.couponNo = couponNo;
        this.adults = adults;
        this.children = children;
        this.applyPayment = applyPayment;
    }

    @Override
    public String toString() {
        return new StringBuilder("PaymentDetailDto{")
                .append("id=").append(id)
                .append(", status=").append(status)
                .append(", amount=").append(amount)
                .append(", remark='").append(remark).append('\'')
                .append(", bookingId=").append(bookingId)
                .append(", invoiceId=").append(invoiceId)
                .append(", transactionDate=").append(transactionDate)
                .append(", firstName='").append(firstName).append('\'')
                .append(", lastName='").append(lastName).append('\'')
                .append(", reservation=").append(reservation)
                .append(", couponNo=").append(couponNo)
                .append(", adults=").append(adults)
                .append(", children=").append(children)
                .append(", createdAt=").append(createdAt)
                .append(", paymentDetailId=").append(paymentDetailId)
                .append(", parentId=").append(parentId)
                .append(", applyDepositValue=").append(applyDepositValue)
                .append(", process=").append(applyPayment)
                .append(", appliedAt=").append(appliedAt)
                .append(", effectiveDate=").append(effectiveDate)
                .append('}')
                .toString();
    }

}
