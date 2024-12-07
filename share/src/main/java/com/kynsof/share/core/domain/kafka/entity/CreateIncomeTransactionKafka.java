package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateIncomeTransactionKafka {
    private UUID id;
    private LocalDateTime invoiceDate;
    private Boolean manual;
    private UUID agency;
    private UUID hotel;
    private UUID invoiceType;
    private UUID invoiceStatus;
    private UUID employeeId;
    private Double incomeAmount;
    private String status;
    private Long invoiceNumber;
    private LocalDate dueDate;
    private Boolean reSend;
    private LocalDate reSendDate;
    private Long invoiceId;
    private String employee;
    private String statusAdjustment;
    private String employeeAdjustment;
    private UUID transactionTypeAdjustment;
    private LocalDate dateAdjustment;
    private String remark;

    private UUID relatedPaymentDetail;

}


