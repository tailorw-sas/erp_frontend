package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsof.share.utils.ScaleAmount;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private OffsetDateTime transactionDate;
    private Double childrenTotalValue = 0.0;
    private OffsetDateTime createdAt;
    private Double applyDepositValue;
    private Boolean hasApplyDeposit;
    private Boolean applyPayment;
    private Long reverseFrom;
    private boolean createByCredit;
    private ManageBookingResponse manageBooking;

    public PaymentDetailResponse(PaymentDetailDto dto) {
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.paymentId = dto.getPayment().getId();
        this.transactionType = dto.getTransactionType() != null ? new ManagePaymentTransactionTypeResponse(dto.getTransactionType()) : null;
        this.amount = dto.getAmount();
        this.remark = dto.getRemark();

        if (dto.getChildren() != null) {
            for (PaymentDetailDto paymentDetailDto : dto.getChildren()) {
                this.childrenTotalValue += ScaleAmount.scaleAmount(paymentDetailDto.getAmount());
                children.add(new PaymentDetailResponse(paymentDetailDto));
            }
        }
        this.transactionDate = dto.getTransactionDate();
        this.createdAt = dto.getCreatedAt();
        this.paymentDetailId = dto.getPaymentDetailId();
        this.parentId = dto.getParentId() != null ? dto.getParentId() : null;
        this.applyDepositValue = dto.getApplyDepositValue() != null ? dto.getApplyDepositValue() : null;
        this.hasApplyDeposit = !this.children.isEmpty();
        this.manageBooking = dto.getManageBooking() != null ? new ManageBookingResponse(dto.getManageBooking()) : null;
        this.applyPayment = dto.getApplayPayment();
        this.reverseFrom = dto.getReverseFrom() != null ? dto.getReverseFrom() : null;
        this.createByCredit = dto.isCreateByCredit();
    }

}
