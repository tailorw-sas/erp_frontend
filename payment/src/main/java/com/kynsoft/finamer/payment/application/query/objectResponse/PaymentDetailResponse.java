package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsof.share.utils.ScaleAmount;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import java.time.OffsetDateTime;
import java.util.ArrayList;
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
public class PaymentDetailResponse implements IResponse {

    private UUID id;
    private Status status;
    private UUID paymentId;
    private Long paymentDetailId;
    private Long parentId;
    private ManagePaymentTransactionTypeResponse transactionType;
    private Double amount;
    private String remark;
    private List<PaymentDetailResponse> children = new ArrayList<>();

    private Double bookingId;
    private String invoiceId;
    private OffsetDateTime transactionDate;
    private String firstName;
    private String lastName;
    private String reservation;
    private String couponNo;
    private Integer adults;
    private Integer childrens;
    private Double childrenTotalValue = 0.0;
    private OffsetDateTime createdAt;
    private Double applyDepositValue;
    private Boolean hasApplyDeposit;
    private Boolean applyPayment;
    private ManageBookingResponse manageBooking;

    public PaymentDetailResponse(PaymentDetailDto dto) {
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.paymentId = dto.getPayment().getId();
        this.transactionType = dto.getTransactionType() != null ? new ManagePaymentTransactionTypeResponse(dto.getTransactionType()) : null;
        this.amount = dto.getAmount();
        this.remark = dto.getRemark();
        //this.children = dto.getChildren() != null ? dto.getChildren().stream().map(PaymentDetailResponse::new).toList() : null;

        if (dto.getChildren() != null) {
            for (PaymentDetailDto paymentDetailDto : dto.getChildren()) {
                this.childrenTotalValue += ScaleAmount.scaleAmount(paymentDetailDto.getAmount());
                children.add(new PaymentDetailResponse(paymentDetailDto));
            }
        }
        this.bookingId = dto.getBookingId();
        this.invoiceId = dto.getInvoiceId();
        this.transactionDate = dto.getTransactionDate();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.reservation = dto.getReservation();
        this.couponNo = dto.getCouponNo();
        this.adults = dto.getAdults();
        this.childrens = dto.getChildrens();
        this.createdAt = dto.getCreatedAt();
        this.paymentDetailId = dto.getPaymentDetailId();
        this.parentId = dto.getParentId() != null ? dto.getParentId() : null;
        this.applyDepositValue = dto.getApplyDepositValue() != null ? dto.getApplyDepositValue() : null;
        this.hasApplyDeposit = !this.children.isEmpty();
        this.manageBooking = dto.getManageBooking() != null ? new ManageBookingResponse(dto.getManageBooking()) : null;
        this.applyPayment = dto.getApplayPayment();
    }

}
