package com.kynsof.share.core.domain.kafka.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
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
    private ReplicatePaymentKafka paymentKafka;
    private AttachmentKafka attachment;

    public CreateIncomeTransactionKafka(UUID id, LocalDateTime invoiceDate, Boolean manual, UUID agency, UUID hotel, UUID invoiceType, UUID invoiceStatus, UUID employeeId, Double incomeAmount, String status, Long invoiceNumber, LocalDate dueDate, Boolean reSend, LocalDate reSendDate, Long invoiceId, String employee, String statusAdjustment, String employeeAdjustment, UUID transactionTypeAdjustment, LocalDate dateAdjustment, String remark, UUID relatedPaymentDetail, ReplicatePaymentKafka paymentKafka) {
        this.id = id;
        this.invoiceDate = invoiceDate;
        this.manual = manual;
        this.agency = agency;
        this.hotel = hotel;
        this.invoiceType = invoiceType;
        this.invoiceStatus = invoiceStatus;
        this.employeeId = employeeId;
        this.incomeAmount = incomeAmount;
        this.status = status;
        this.invoiceNumber = invoiceNumber;
        this.dueDate = dueDate;
        this.reSend = reSend;
        this.reSendDate = reSendDate;
        this.invoiceId = invoiceId;
        this.employee = employee;
        this.statusAdjustment = statusAdjustment;
        this.employeeAdjustment = employeeAdjustment;
        this.transactionTypeAdjustment = transactionTypeAdjustment;
        this.dateAdjustment = dateAdjustment;
        this.remark = remark;
        this.relatedPaymentDetail = relatedPaymentDetail;
        this.paymentKafka = paymentKafka;
    }
}


