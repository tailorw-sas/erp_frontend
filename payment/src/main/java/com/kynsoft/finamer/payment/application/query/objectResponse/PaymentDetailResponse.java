package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private ManagePaymentTransactionTypeResponse transactionType;
    private Double amount;
    private String remark;
    private List<PaymentDetailResponse> children;

    private Double bookingId;
    private String invoiceId;
    private LocalDate transactionDate;
    private String firstName;
    private String lastName;
    private String reservation;
    private String couponNo;
    private Integer adults;
    private Integer childrens;
    private LocalDateTime createdAt;

    public PaymentDetailResponse(PaymentDetailDto dto) {
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.paymentId = dto.getPayment().getId();
        this.transactionType = dto.getTransactionType() != null ? new ManagePaymentTransactionTypeResponse(dto.getTransactionType()) : null;
        this.amount = dto.getAmount();
        this.remark = dto.getRemark();
        this.children = dto.getChildren() != null ? dto.getChildren().stream().map(PaymentDetailResponse::new).toList() : null;

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
    }

}
