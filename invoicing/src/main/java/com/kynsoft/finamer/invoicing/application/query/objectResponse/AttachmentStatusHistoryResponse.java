package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.AttachmentStatusHistoryDto;
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
public class AttachmentStatusHistoryResponse implements IResponse {

    private UUID id;
    private String description;
    private Long attachmentId;
    private ManageInvoiceResponse invoice;
    private String employee;
    private UUID employeeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AttachmentStatusHistoryResponse(AttachmentStatusHistoryDto dto){
        this.id = dto.getId();
        this.description = dto.getDescription();
        this.attachmentId = dto.getAttachmentId();
        this.invoice = dto.getInvoice() != null ? new ManageInvoiceResponse(dto.getInvoice()) : null;
        this.employee = dto.getEmployee();
        this.employeeId = dto.getEmployeeId();
        this.createdAt = dto.getCreatedAt();
        this.updatedAt = dto.getUpdatedAt();
    }
}
