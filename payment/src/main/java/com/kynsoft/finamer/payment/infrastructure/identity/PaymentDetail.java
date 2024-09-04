package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsof.share.utils.ScaleAmount;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payment_detail")
public class PaymentDetail implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition="serial", name = "payment_detail_gen_id")
    @Generated(event = EventType.INSERT)
    private Long paymentDetailId;

    private Long parentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_type_id")
    private ManagePaymentTransactionType transactionType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_booking_id")
    private ManageBooking manageBooking;

    private Double amount;
    private Double applyDepositValue;
    private String remark;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean applayPayment;

    private Double bookingId;
    private String invoiceId;
    private OffsetDateTime transactionDate;
    private String firstName;
    private String lastName;
    private String reservation;
    private String couponNo;
    private Integer adults;
    private Integer childrens;

    @OneToMany(fetch = FetchType.EAGER)
    private List<PaymentDetail> children = new ArrayList<>();

    //@CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void prePersist() {
        this.createdAt = OffsetDateTime.now(ZoneId.of("UTC"));
    }

    public PaymentDetail(PaymentDetailDto dto) {
        this.id = dto.getId();
        this.payment = dto.getPayment() != null ? new Payment(dto.getPayment()) : null;
        this.transactionType = dto.getTransactionType() != null ? new ManagePaymentTransactionType(dto.getTransactionType()) : null;
        this.amount = ScaleAmount.scaleAmount(dto.getAmount());
        this.remark = dto.getRemark();
        this.status = dto.getStatus();
        if (dto.getChildren() != null) {
            this.children = dto.getChildren().stream()
                    .map(PaymentDetail::new)
                    .collect(Collectors.toList());
        }
        this.bookingId = dto.getBookingId() != null ? dto.getBookingId() : null;
        this.invoiceId = dto.getInvoiceId() != null ? dto.getInvoiceId() : null;
        this.transactionDate = dto.getTransactionDate() != null ? dto.getTransactionDate() : null;
        this.firstName = dto.getFirstName() != null ? dto.getFirstName() : null;
        this.lastName = dto.getLastName() != null ? dto.getLastName() : null;
        this.reservation = dto.getReservation() != null ? dto.getReservation() : null;
        this.couponNo = dto.getCouponNo() != null ? dto.getCouponNo() : null;
        this.adults = dto.getAdults() != null ? dto.getAdults() : null;
        this.childrens = dto.getChildrens() != null ? dto.getChildrens() : null;
        this.parentId = dto.getParentId() != null ? dto.getParentId() : null;
        this.applyDepositValue = dto.getApplyDepositValue() != null ? ScaleAmount.scaleAmount(dto.getApplyDepositValue()) : null;
        this.manageBooking = dto.getManageBooking() != null ? new ManageBooking(dto.getManageBooking()) : null;
        this.applayPayment = dto.getApplayPayment();
    }

    public PaymentDetailDto toAggregate() {

        return new PaymentDetailDto(
                id,
                status,
                payment.toAggregate(),
                transactionType.toAggregate(),
                amount,
                remark,
                children != null ? children.stream().map(PaymentDetail::toAggregateSimple).toList() : null,
                manageBooking != null ? manageBooking.toAggregate() : null,
                bookingId,
                invoiceId,
                transactionDate,
                firstName,
                lastName,
                reservation,
                couponNo,
                adults,
                childrens,
                createdAt,
                paymentDetailId,
                parentId,
                applyDepositValue,
                applayPayment
        );
    }

    public PaymentDetailDto toAggregateSimple() {

        return new PaymentDetailDto(
                id,
                status,
                payment.toAggregate(),
                transactionType.toAggregate(),
                amount,
                remark,
                null,
                manageBooking != null ? manageBooking.toAggregate() : null,
                bookingId != null ? bookingId : null,
                invoiceId != null ? invoiceId : null,
                transactionDate != null ? transactionDate : null,
                firstName != null ? firstName : null,
                lastName != null ? lastName : null,
                reservation != null ? reservation : null,
                couponNo != null ? couponNo : null,
                adults != null ? adults : null,
                childrens != null ? childrens : null,
                createdAt,
                paymentDetailId,
                parentId,
                applyDepositValue,
                applayPayment
        );
    }

    public PaymentDetailDto toAggregateSimpleNotPayment() {

        return new PaymentDetailDto(
                id,
                status,
                null,
                transactionType.toAggregate(),
                amount,
                remark,
                null,
                manageBooking != null ? manageBooking.toAggregate() : null,
                bookingId != null ? bookingId : null,
                invoiceId != null ? invoiceId : null,
                transactionDate != null ? transactionDate : null,
                firstName != null ? firstName : null,
                lastName != null ? lastName : null,
                reservation != null ? reservation : null,
                couponNo != null ? couponNo : null,
                adults != null ? adults : null,
                childrens != null ? childrens : null,
                createdAt,
                paymentDetailId,
                parentId,
                applyDepositValue,
                applayPayment
        );
    }
}
