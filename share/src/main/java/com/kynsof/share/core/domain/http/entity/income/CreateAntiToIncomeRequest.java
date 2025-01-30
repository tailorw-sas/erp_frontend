package com.kynsof.share.core.domain.http.entity.income;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateAntiToIncomeRequest {

    private String invoiceDate;
    private Boolean manual;
    private UUID agency;
    private UUID hotel;
    private UUID invoiceType;
    private UUID invoiceStatus;
    private Double incomeAmount;
    private String status;
    private Long invoiceNumber;
    private String dueDate;
    private Boolean reSend;
    private String reSendDate;
    private String employee;
    private List<CreateAntiToIncomeAttachmentRequest> attachments;
}
