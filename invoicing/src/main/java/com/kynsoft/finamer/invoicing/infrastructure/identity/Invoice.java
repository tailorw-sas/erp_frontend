package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.ImportType;
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
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "invoice")
//@EntityListeners(AuditEntityListener.class)
//@RemoteAudit(name = "invoice",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class Invoice {

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

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean autoRec;

    private Boolean reSend;
    private Boolean isCloned;

    private LocalDate reSendDate;

    @ManyToOne(fetch = FetchType.EAGER)
    private Invoice parent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_type_id")
    private ManageInvoiceType manageInvoiceType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_status_id")
    private ManageInvoiceStatus manageInvoiceStatus;

    private Double originalAmount;
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

    @Enumerated(EnumType.STRING)
    private ImportType importType;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "invoice", cascade = CascadeType.MERGE)
    private List<ManageAttachment> attachments;

    private Double credits;

    private String sendStatusError;
    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean hasAttachments = false;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean deleteInvoice;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;
    @Column(nullable = false,columnDefinition = "integer default 0")
    private Integer aging;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean cloneParent;

    public Invoice(ManageInvoiceDto dto) {
        this.id = dto.getId();
        this.invoiceNumber = dto.getInvoiceNumber();
        this.invoiceDate = dto.getInvoiceDate();
        this.isManual = dto.getIsManual();
        this.invoiceAmount = Objects.nonNull(dto.getInvoiceAmount()) ? BankerRounding.round(dto.getInvoiceAmount()) : null;
        this.originalAmount = Objects.nonNull(dto.getOriginalAmount()) ? BankerRounding.round(dto.getOriginalAmount()) : null;
        this.hotel = Objects.nonNull(dto.getHotel()) ? new ManageHotel(dto.getHotel()) : null;
        this.agency = Objects.nonNull(dto.getAgency()) ? new ManageAgency(dto.getAgency()) : null;
        this.invoiceType = Objects.nonNull(dto.getInvoiceType()) ? dto.getInvoiceType() : EInvoiceType.INVOICE;
        this.invoiceStatus = dto.getStatus();
        this.autoRec = Objects.nonNull(dto.getAutoRec()) ? dto.getAutoRec() : false;
        this.bookings = Objects.nonNull(dto.getBookings()) ? dto.getBookings().stream().map(_booking -> {
            Booking booking = new Booking(_booking);
            booking.setInvoice(this);
            return booking;
        }).collect(Collectors.toList()) : null;
        this.dueDate = dto.getDueDate();
        this.attachments = Objects.nonNull(dto.getAttachments()) ? dto.getAttachments().stream().map(_attachment -> {
            ManageAttachment attachment = new ManageAttachment(_attachment);
            attachment.setInvoice(this);
            return attachment;
        }).collect(Collectors.toList()) : null;
        this.reSend = dto.getReSend();
        this.reSendDate = dto.getReSendDate();
        this.manageInvoiceType = Objects.nonNull(dto.getManageInvoiceType()) ? new ManageInvoiceType(dto.getManageInvoiceType())
                : null;
        this.manageInvoiceStatus = Objects.nonNull(dto.getManageInvoiceStatus()) ?
                new ManageInvoiceStatus(dto.getManageInvoiceStatus())
                : null;
        this.dueAmount = Objects.nonNull(dto.getDueAmount()) ? BankerRounding.round(dto.getDueAmount()) : 0.0;
        this.invoiceNo = dto.getInvoiceNo();
        this.invoiceNumberPrefix = dto.getInvoiceNumberPrefix();
        this.isCloned = dto.getIsCloned();
        this.parent = Objects.nonNull(dto.getParent()) ? new Invoice(dto.getParent()) : null;
        this.credits = dto.getCredits();
        this.sendStatusError = dto.getSendStatusError();
        this.aging = dto.getAging();
        this.importType = Objects.nonNull(dto.getImportType()) ? dto.getImportType() : ImportType.NONE;
        this.deleteInvoice = dto.isDeleteInvoice();
        this.cloneParent = dto.isCloneParent();
        this.hasAttachments = Objects.nonNull(attachments) && !attachments.isEmpty();
    }

    public ManageInvoiceDto toAggregateSimple() {
        ManageInvoiceDto manageInvoiceDto = new ManageInvoiceDto(
                this.id,
                this.invoiceId,
                this.invoiceNo,
                this.invoiceNumber,
                this.invoiceNumberPrefix,
                this.invoiceDate,
                this.dueDate,
                this.isManual,
                Objects.nonNull(this.invoiceAmount) ? BankerRounding.round(invoiceAmount) : null,
                Objects.nonNull(this.dueAmount) ? BankerRounding.round(dueAmount) : null,
                Objects.nonNull(this.hotel) ? this.hotel.toAggregate() : null,
                Objects.nonNull(this.agency) ? this.agency.toAggregate() : null,
                this.invoiceType,
                this.invoiceStatus,
                this.autoRec,
                null,
                null,
                this.reSend,
                this.reSendDate,
                Objects.nonNull(this.manageInvoiceType) ? manageInvoiceType.toAggregate() : null,
                Objects.nonNull(this.manageInvoiceStatus) ? manageInvoiceStatus.toAggregate() : null,
                this.createdAt,
                this.isCloned,
                null,
                this.credits,
                this.aging);

        manageInvoiceDto.setOriginalAmount(Objects.nonNull(this.originalAmount) ? BankerRounding.round(this.originalAmount) : null);
        manageInvoiceDto.setImportType(Objects.nonNull(this.importType) ? this.importType : ImportType.NONE);
        manageInvoiceDto.setDeleteInvoice(Objects.nonNull(this.deleteInvoice) && this.deleteInvoice);
        manageInvoiceDto.setCloneParent(Objects.nonNull(this.cloneParent) && this.cloneParent);
        return manageInvoiceDto;
    }

    public ManageInvoiceDto toAggregate() {
        ManageInvoiceDto manageInvoiceDto = new ManageInvoiceDto(
                this.id,
                this.invoiceId,
                this.invoiceNo,
                this.invoiceNumber,
                this.invoiceNumberPrefix,
                this.invoiceDate,
                this.dueDate,
                this.isManual,
                Objects.nonNull(this.invoiceAmount) ? BankerRounding.round(this.invoiceAmount) : null,
                Objects.nonNull(this.dueAmount) ? BankerRounding.round(this.dueAmount) : null,
                Objects.nonNull(this.hotel) ? this.hotel.toAggregate() : null,
                Objects.nonNull(this.agency) ? this.agency.toAggregate() : null,
                this.invoiceType,
                this.invoiceStatus,
                this.autoRec,
                Objects.nonNull(this.bookings) ? this.bookings.stream().map(Booking::toAggregate).collect(Collectors.toList()) : null,
                Objects.nonNull(this.attachments) ? this.attachments.stream().map(ManageAttachment::toAggregateSimple).collect(Collectors.toList()) : null,
                this.reSend,
                this.reSendDate,
                Objects.nonNull(this.manageInvoiceType) ? this.manageInvoiceType.toAggregate() : null,
                Objects.nonNull(this.manageInvoiceStatus) ? this.manageInvoiceStatus.toAggregate() : null,
                this.createdAt,
                this.isCloned,
                Objects.nonNull(this.parent) ? this.parent.toAggregateSimple() : null,
                this.credits,
                this.aging);

        manageInvoiceDto.setOriginalAmount(Objects.nonNull(this.originalAmount) ? BankerRounding.round(this.originalAmount) : null);
        manageInvoiceDto.setImportType(Objects.nonNull(this.importType) ? this.importType : ImportType.NONE);
        manageInvoiceDto.setDeleteInvoice(Objects.nonNull(this.deleteInvoice) && this.deleteInvoice);
        manageInvoiceDto.setCloneParent(Objects.nonNull(this.cloneParent) && this.cloneParent);
        return manageInvoiceDto;
    }

    public ManageInvoiceDto toAggregateSearch() {

        ManageInvoiceDto manageInvoiceDto = new ManageInvoiceDto(this.id,
                this.invoiceId,
                this.invoiceNo,
                this.invoiceNumber,
                this.invoiceNumberPrefix,
                this.invoiceDate,
                this.dueDate,
                this.isManual,
                Objects.nonNull(this.invoiceAmount) ? BankerRounding.round(this.invoiceAmount) : null,
                Objects.nonNull(this.dueAmount) ? BankerRounding.round(this.dueAmount) : null,
                Objects.nonNull(this.hotel) ? this.hotel.toAggregate() : null,
                Objects.nonNull(this.agency) ? this.agency.toAggregate() : null,
                this.invoiceType,
                this.invoiceStatus,
                this.autoRec,
                null,
                null,
                this.reSend,
                this.reSendDate,
                Objects.nonNull(this.manageInvoiceType) ? this.manageInvoiceType.toAggregate() : null,
                Objects.nonNull(this.manageInvoiceStatus) ? this.manageInvoiceStatus.toAggregate() : null,
                this.createdAt,
                this.isCloned,
                Objects.nonNull(this.parent) ? this.parent.toAggregateSimple() : null,
                this.credits,
                this.aging);

        manageInvoiceDto.setSendStatusError(this.sendStatusError);
        manageInvoiceDto.setOriginalAmount(Objects.nonNull(this.originalAmount) ? BankerRounding.round(this.originalAmount) : null);
        manageInvoiceDto.setImportType(Objects.nonNull(this.importType) ? this.importType : ImportType.NONE);
        manageInvoiceDto.setDeleteInvoice(Objects.nonNull(this.deleteInvoice) && this.deleteInvoice);
        manageInvoiceDto.setCloneParent(Objects.nonNull(this.cloneParent) && this.cloneParent);
        return manageInvoiceDto;
    }

    @PostLoad
    public void initDefaultValue() {
        if (Objects.isNull(this.dueAmount)) {
            this.dueAmount = 0.0;
        }
        this.hasAttachments = (Objects.nonNull(this.attachments) && !this.attachments.isEmpty());
    }

}
