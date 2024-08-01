package com.kynsoft.finamer.invoicing.domain.dto;

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
public class AttachmentStatusHistoryDto {

    private UUID id;
    private String description;
    private Long attachmentId;
    private ManageInvoiceDto invoice;
    private String employee;
    private UUID employeeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
