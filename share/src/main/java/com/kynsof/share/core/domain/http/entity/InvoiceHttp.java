package com.kynsof.share.core.domain.http.entity;

import com.kynsof.share.core.domain.bus.query.IResponse;
import java.io.Serializable;
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
public class InvoiceHttp implements IResponse, Serializable {

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
    private List<AttachmentHttp> attachments;
    private List<BookingHttp> bookings;
    private Boolean hasAttachment;
    private String invoiceDate;
    //private LocalDateTime invoiceDate;
    private Boolean autoRec;
}
