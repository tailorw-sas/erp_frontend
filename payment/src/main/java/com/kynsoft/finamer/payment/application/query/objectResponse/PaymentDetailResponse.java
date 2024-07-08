package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
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
    private PaymentDetailResponse parent;

    public PaymentDetailResponse(PaymentDetailDto dto) {
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.paymentId = dto.getPayment().getId();
        this.transactionType = dto.getTransactionType() != null ? new ManagePaymentTransactionTypeResponse(dto.getTransactionType()) : null;
        this.amount = dto.getAmount();
        this.remark = dto.getRemark();
        this.parent = dto.getParent() != null ? new PaymentDetailResponse(dto.getParent()) : null;
    }

}
