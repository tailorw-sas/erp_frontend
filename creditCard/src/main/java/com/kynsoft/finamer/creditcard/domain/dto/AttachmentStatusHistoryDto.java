package com.kynsoft.finamer.creditcard.domain.dto;

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
    private TransactionDto transaction;
    private String employee;
    private UUID employeeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
