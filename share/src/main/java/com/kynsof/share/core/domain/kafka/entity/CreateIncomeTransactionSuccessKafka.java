package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateIncomeTransactionSuccessKafka {
    private UUID id;
    private UUID hotel;
    private UUID client;
    private UUID invoiceParent;
    private UUID agency;
    private Long invoiceId;
    private Long invoiceNo;
    private String invoiceNumber;
    private String invoiceType;
    private Double invoiceAmount;
    private List<ManageBookingKafka> bookings;
    private LocalDateTime invoiceDate;
    private UUID relatedPaymentDetailIds;
    private UUID employeeId;
}
