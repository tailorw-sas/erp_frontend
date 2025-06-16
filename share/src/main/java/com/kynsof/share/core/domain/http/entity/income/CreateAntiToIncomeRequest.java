package com.kynsof.share.core.domain.http.entity.income;

import com.kynsof.share.core.domain.http.entity.income.adjustment.CreateAntiToIncomeAdjustmentRequest;
import com.kynsof.share.core.domain.http.entity.income.attachment.CreateAntiToIncomeAttachmentRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateAntiToIncomeRequest {
    private LocalDateTime invoiceDate;
    private Boolean manual;
    private UUID agency;
    private UUID hotel;
    private UUID invoiceType;
    private UUID invoiceStatus;
    private Double incomeAmount;
    private String status;
    private Long invoiceNumber;
    private LocalDate dueDate;
    private Boolean reSend;
    private LocalDate reSendDate;
    private String employee;
    private List<CreateAntiToIncomeAttachmentRequest> attachments;
    private List<CreateAntiToIncomeAdjustmentRequest> adjustments;
}
