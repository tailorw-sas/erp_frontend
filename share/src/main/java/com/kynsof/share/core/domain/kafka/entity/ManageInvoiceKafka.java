package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageInvoiceKafka {

    private UUID id;
    private UUID hotel;
    private UUID client;
    private UUID agency;
    private Long invoiceId;
    private Long invoiceNo;
    private String invoiceNumber;
    private String invoiceType;
    private Double invoiceAmount;
    private List<ManageBookingKafka> bookings;
    private List<AttachmentKafka> attachments;
    private Boolean hasAttachment;
}
