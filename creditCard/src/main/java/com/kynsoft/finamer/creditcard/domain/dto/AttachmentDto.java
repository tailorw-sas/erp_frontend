package com.kynsoft.finamer.creditcard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDto {
    private UUID id;
    private Long attachmentId;
    private String filename;
    private String file;
    private String remark;
    private ManageAttachmentTypeDto type;
    private TransactionDto transaction;
    private String employee;
    private UUID employeeId;
    private LocalDateTime createdAt;
    private ResourceTypeDto paymentResourceType;

    public AttachmentDto(AttachmentDto dto) {
        this.id = UUID.randomUUID();
        this.attachmentId = dto.getAttachmentId();
        this.filename = dto.getFilename();
        this.file = dto.getFile();
        this.remark = dto.getRemark();
        this.type = dto.getType();
        this.transaction = dto.getTransaction();
        this.employee = dto.getEmployee();
        this.employeeId = dto.getEmployeeId();
        this.createdAt = dto.getCreatedAt();
        this.paymentResourceType = dto.getPaymentResourceType();
    }
}
