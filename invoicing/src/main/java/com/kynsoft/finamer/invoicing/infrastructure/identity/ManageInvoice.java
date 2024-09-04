package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_invoice")
public class ManageInvoice {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(columnDefinition = "serial", name = "invoice_gen_id")
    @Generated(event = EventType.INSERT)
    private Long invoiceId;

    private Long invoiceNo;

    private String invoiceNumber;
    private String invoiceNumberPrefix;

    private LocalDateTime invoiceDate;

    private LocalDate dueDate;

    private Boolean isManual;
    private Boolean autoRec;

    private Boolean reSend;
    private Boolean isCloned;

    private LocalDate reSendDate;

    @ManyToOne(fetch = FetchType.EAGER)
    private ManageInvoice parent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id")
    private ManageInvoiceType manageInvoiceType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_status_id")
    private ManageInvoiceStatus manageInvoiceStatus;

    private Double invoiceAmount;

    @Column(name = "due_amount", nullable = false)
    @ColumnDefault("0")
    private Double dueAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_hotel")
    private ManageHotel hotel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_agency")
    private ManageAgency agency;

    @Enumerated(EnumType.STRING)
    private EInvoiceType invoiceType;

    @Enumerated(EnumType.STRING)
    private EInvoiceStatus invoiceStatus;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<ManageBooking> bookings;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "invoice", cascade = CascadeType.MERGE)
    private List<ManageAttachment> attachments;

    @Column(nullable = true)
    private Boolean deleted = false;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Double credits;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deletedAt;

    public ManageInvoice(ManageInvoiceDto dto) {
        this.id = dto.getId();
        this.invoiceNumber = dto.getInvoiceNumber();
        this.invoiceDate = dto.getInvoiceDate();
        this.isManual = dto.getIsManual();
        this.invoiceAmount = dto.getInvoiceAmount();
        this.hotel = dto.getHotel() != null ? new ManageHotel(dto.getHotel()) : null;
        this.agency = dto.getAgency() != null ? new ManageAgency(dto.getAgency()) : null;
        this.invoiceType = dto.getInvoiceType() != null ? dto.getInvoiceType() : EInvoiceType.INVOICE;
        this.invoiceStatus = dto.getStatus();
        this.autoRec = false;
        this.bookings = dto.getBookings() != null ? dto.getBookings().stream().map(_booking -> {
            ManageBooking booking = new ManageBooking(_booking);
            booking.setInvoice(this);
            return booking;
        }).collect(Collectors.toList()) : null;
        this.dueDate = dto.getDueDate();
        this.attachments = dto.getAttachments() != null ? dto.getAttachments().stream().map(_attachment -> {
            ManageAttachment attachment = new ManageAttachment(_attachment);
            attachment.setInvoice(this);
            return attachment;
        }).collect(Collectors.toList()) : null;
        this.reSend = dto.getReSend();
        this.reSendDate = dto.getReSendDate();
        this.manageInvoiceType = dto.getManageInvoiceType() != null ? new ManageInvoiceType(dto.getManageInvoiceType())
                : null;
        this.manageInvoiceStatus = dto.getManageInvoiceStatus() != null
                ? new ManageInvoiceStatus(dto.getManageInvoiceStatus())
                : null;
        this.dueAmount = dto.getDueAmount() != null ? dto.getDueAmount() : 0.0;
        this.invoiceNo = dto.getInvoiceNo();

        if (dto.getInvoiceNumber() != null) {
            int firstIndex = dto.getInvoiceNumber().indexOf("-");
            int lastIndex = dto.getInvoiceNumber().lastIndexOf("-");

            if (firstIndex != -1 && lastIndex != -1 && firstIndex != lastIndex) {
                String beginInvoiceNumber = dto.getInvoiceNumber().substring(0, firstIndex);
                String lastInvoiceNumber = dto.getInvoiceNumber().substring(lastIndex + 1,
                        dto.getInvoiceNumber().length());

                invoiceNumberPrefix = beginInvoiceNumber + "-" + lastInvoiceNumber;

            } else {
                invoiceNumberPrefix = dto.getInvoiceNumber();
            }

            if (lastIndex != -1) {
                try {

                    this.invoiceNo = Long.parseLong(dto.getInvoiceNumber().substring(lastIndex + 1));

                } catch (NumberFormatException e) {
                    invoiceNo = invoiceId;
                }
            }
        }

        this.isCloned = dto.getIsCloned();
        this.parent = dto.getParent() != null ? new ManageInvoice(dto.getParent()) : null;
        this.credits = dto.getCredits();
    }

    public ManageInvoiceDto toAggregateSample() {

        return new ManageInvoiceDto(id, invoiceId, invoiceNo, invoiceNumber, invoiceDate, dueDate, isManual,
                invoiceAmount, dueAmount,
                hotel.toAggregate(), agency.toAggregate(), invoiceType, invoiceStatus,
                autoRec, null, null, reSend, reSendDate,
                manageInvoiceType != null ? manageInvoiceType.toAggregate() : null,
                manageInvoiceStatus != null ? manageInvoiceStatus.toAggregate() : null, createdAt, isCloned,
                parent != null ? parent.toAggregate() : null, credits);

    }

    public ManageInvoiceDto toAggregate() {
        return new ManageInvoiceDto(id, invoiceId,
                invoiceNo, invoiceNumber, invoiceDate, dueDate, isManual, invoiceAmount, dueAmount,
                hotel.toAggregate(), agency.toAggregate(), invoiceType, invoiceStatus,
                autoRec, bookings != null ? bookings.stream().map(b -> {
                    return b.toAggregateSample();
                }).collect(Collectors.toList()) : null, attachments != null ? attachments.stream().map(b -> {
                    return b.toAggregateSample();
                }).collect(Collectors.toList()) : null,
                reSend,
                reSendDate,
                manageInvoiceType != null ? manageInvoiceType.toAggregate() : null,
                manageInvoiceStatus != null ? manageInvoiceStatus.toAggregate() : null, createdAt, isCloned,
                parent != null ? parent.toAggregate() : null, credits);
    }

    @PostLoad
    public void initDefaultValue() {
        if (dueAmount == null) {
            dueAmount = 0.0;
        }

    }

}
