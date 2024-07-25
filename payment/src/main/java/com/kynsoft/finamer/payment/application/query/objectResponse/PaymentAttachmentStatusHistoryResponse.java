package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentAttachmentStatusHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentAttachmentStatusHistoryResponse implements IResponse {

    private UUID id;
    private String status;
    private PaymentResponse payment;
    private ManageEmployeeResponse employee;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PaymentAttachmentStatusHistoryResponse(PaymentAttachmentStatusHistoryDto dto) {
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.payment = dto.getPayment() != null ? new PaymentResponse(dto.getPayment()) : null;
        this.employee = dto.getEmployee() != null ? new ManageEmployeeResponse(dto.getEmployee()) : null;
        this.description = dto.getDescription();
        this.createdAt = dto.getCreatedAt();
        this.updatedAt = dto.getUpdatedAt();
    }

}
